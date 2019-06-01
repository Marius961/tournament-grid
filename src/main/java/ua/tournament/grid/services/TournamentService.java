package ua.tournament.grid.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.tournament.grid.entities.*;
import ua.tournament.grid.exceptions.NotFoundException;
import ua.tournament.grid.repo.*;

import java.util.*;

@Service
public class TournamentService {

    private final TournamentRepo tournamentRepo;
    private final TeamRepo teamRepo;
    private final TournamentTeamRepo tournamentTeamRepo;
    private final MatchService matchService;
    private final StageRepo stageRepo;

    @Autowired
    public TournamentService(TournamentRepo tournamentRepo, TeamRepo teamRepo, TournamentTeamRepo tournamentTeamRepo, MatchService matchService, StageRepo stageRepo) {
        this.tournamentRepo = tournamentRepo;
        this.teamRepo = teamRepo;
        this.tournamentTeamRepo = tournamentTeamRepo;
        this.matchService = matchService;
        this.stageRepo = stageRepo;
    }


    // Метод для створення турніру, приймає єкземпляр класу Tournament, який приходить з клієнту
    public void createTournament(Tournament tournament) throws NotFoundException {

        // Збереження кількості команд турніру у змінну
        int teamsCount = tournament.getTournamentTeams().size();

        // Перевірка чи відвовідає кількість команд у турнірі значенням: 2, 4, 8, 16, 32, 64
        if (teamsCount == 2 || teamsCount == 4 || teamsCount == 8 || teamsCount == 16 || teamsCount == 32 || teamsCount == 64) {
            // Перевірка чи дата початку турніру встановлена перед датою закінчення турніру
            if (tournament.getStartDate().before(tournament.getEndDate())) {

                // Ініціалізація структури типу Set, яка буде зберігати підтверджені команди турніру
                Set<TournamentTeam> verifiedTournamentTeams = new HashSet<>();

                // Генерація перемішаного списку порядкових номерів команд
                List<Integer> sequentNumbers = getShuffledNumbersArray(teamsCount);

                // Індекс першої команди
                int index = 0;

                // Перебір даних отриманих з клієнту (перевірка команд у турнірі)
                for (TournamentTeam tournamentTeam : tournament.getTournamentTeams()) {

                    // Спроба завантажити команду з бази даних по імені
                    Optional<Team> opTeam = teamRepo.findByName(tournamentTeam.getTeam().getName());

                    // Присвоєння команди до турніру
                    tournamentTeam.setTournament(tournament);
                    // додавання команді порядкового номеру із згенерованого вище списку номерів команд
                    tournamentTeam.setSequentNumber(sequentNumbers.get(index));
                    // Інкрементування індексу
                    index++;

                    // якщо команда вибрана вище з вказаним іменем існує в базі даних
                    if (opTeam.isPresent()) {
                        // Дані не додаються, а беруться з бази даних, та присвоюються в клас tournamentTeam
                        tournamentTeam.setTeam(opTeam.get());

                        // Перевірка на наявність команди у списку підтверджених (запобігає повторному додаванню двох однакових команд в один турнір)
                        if (!isTeamAlreadyAdded(verifiedTournamentTeams, opTeam.get())) {
                            // Якщо команди у турнірі немає - команда додається у турнір.
                            verifiedTournamentTeams.add(tournamentTeam);
                        // в іншому випадку повернути виключення з повідомленням про неможливість додати 2 однакові команди
                        } else throw new NotFoundException("Cannot add 2 same teams");
                    // Якщо команди немає в БД - команду буде додано
                    } else {
                        // Створюється екземпляр класу Team та ініціалізується з даними отриманого турніру
                        Team newTeam = tournamentTeam.getTeam();
                        // Збереження команди у базу даних
                        // Після збереження даних, об'єкту newTeam у поле id буде автоматично присвоєно id яке створено у БД
                        teamRepo.save(newTeam);
                        // перевірка чи команда не була додана раніше (вірогідність такого сценарію дуже низька так як команда яку додали у БД пройде по першій умові)
                        if (!isTeamAlreadyAdded(verifiedTournamentTeams, tournamentTeam.getTeam())) {
                            // Для команди в турнірі встановлюється id null щоб запобігти перезапису існуючих даних з id який може збігатись
                            tournamentTeam.setId(null);
                            // У команду турніру додається раніше збережена у БД команда
                            tournamentTeam.setTeam(newTeam);
                            // Так як у команди турніру присутній і порядковий номер і команда - її можна додати у список підтверджених команд
                            verifiedTournamentTeams.add(tournamentTeam);
                        // Повертається виключення якщо команда з таким іменем вже у турнірі
                        } else throw new NotFoundException("Cannot add 2 same teams");
                    }
                }
                // Отримання з бази даних етапу, який підходить для поточної кількості команд
                Optional<Stage> opStage = stageRepo.findByRequiredTeamsCount(teamsCount);
                // Якщо етап для вказаної кількості існує - тоді турніру присвоюється поточний етап
                opStage.ifPresent(tournament::setCurrentStage);
                /*
                    Якщо кількість команд не співпадає з кінцевою кількістю підтверджених команд - тоді повертається виключення
                    Таке можливо, якщо додаються дві однакові команди, вони не проходять перевірку, в результаті їх не додає у список
                    після чого кількість команд які підходять відрізняється від загальної кількості
                 */
                if (teamsCount == verifiedTournamentTeams.size()) {
                    // Встановлення id null для запобіганню перезапису
                    tournament.setId(null);
                    // збереження турніру у базі даних
                    tournamentRepo.save(tournament);
                    /*
                     Так як вище командам було присвоєно посилання на турнір, а після збереження турніру, йому автоматично встановилось id,
                     тому всі команди які додаються вже мають в собі id турніру, який вже був доданих, тим самим прозодять валідацію
                     */
                    tournamentTeamRepo.saveAll(verifiedTournamentTeams);
                    // після того як турнір повністю сформована, для нього створюються матчі
                    matchService.createMatchesForTournament(tournament.getId());
                // Повертається при додаванні двох однакових команд у турнір
                } else throw new NotFoundException("Can't add two teams with the same names to tournament");
            // Повертається при некоректному вказуванні дати початку та кінця (а точніше якщо вказати дату кінця швидше ніж початку)
            } else throw new IllegalArgumentException("Tournament start date mist be before end date");
        // Повертається при некоректній кількості команд у турнірі
        } else throw new NotFoundException("Count of teams in tournament must be 2, 4, 8, 16, 32 or 64. In your tournament " + teamsCount + " teams");
    }

    // генерація перемішаного списку порядкових номерів команд
    private List<Integer> getShuffledNumbersArray(int maxNumber) {
        // створення структури (списку) класу ArrayList
        ArrayList<Integer> list = new ArrayList<>();
        // цикл, який додає у список цілі числа, кількість проходів це кількість команд у турнірі яка передається параметром методу
        for (int i = 0; i < maxNumber; i++) {
            // Додавання числа у список, додається 1 так як початок відліку починається з одиниці а не з нуля
            list.add(i+1);
        }
        // Перемішування елементів списку за допомогою статичного класу Collections з методом shuffle
        Collections.shuffle(list);
        // Метод повертає згенерований та перемішаний список
        return list;
    }

    public Page<Tournament> getAllTournaments(int page) {
        return tournamentRepo.findAll(PageRequest.of(page, 16, Sort.by("id").descending()));
    }

    public Tournament getTournament(Long id) throws NotFoundException {
        return tournamentRepo.findFirstById(id).orElseThrow(() -> new NotFoundException("Cannot find tournament"));
    }

    public void deleteTournament(Long tournamentId) throws NotFoundException {
        if (tournamentRepo.existsById(tournamentId)) {
            tournamentRepo.deleteById(tournamentId);
        } else throw new NotFoundException("Cannot find tournament");
    }

    // Перевірка наявності команди яка передається як другий параметр, на наявність у списку який передається першим параметром
    private boolean isTeamAlreadyAdded(Set<TournamentTeam> teams, Team team) {
        for (TournamentTeam tournamentTeam: teams) {
            // Якщо команда присутня у списку повертає true
            if (tournamentTeam.getTeam().equals(team)) return true;
        }
        return false;
    }
}
