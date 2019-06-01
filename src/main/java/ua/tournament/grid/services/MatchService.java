package ua.tournament.grid.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.tournament.grid.entities.Match;
import ua.tournament.grid.entities.Stage;
import ua.tournament.grid.entities.Tournament;
import ua.tournament.grid.entities.TournamentTeam;
import ua.tournament.grid.exceptions.NotFoundException;
import ua.tournament.grid.repo.MatchRepo;
import ua.tournament.grid.repo.StageRepo;
import ua.tournament.grid.repo.TournamentRepo;
import ua.tournament.grid.repo.TournamentTeamRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepo matchRepo;
    private final TournamentRepo tournamentRepo;
    private final StageRepo stageRepo;
    private final TournamentTeamRepo tournamentTeamRepo;

    @Autowired
    public MatchService(MatchRepo matchRepo, TournamentRepo tournamentRepo, StageRepo stageRepo, TournamentTeamRepo tournamentTeamRepo) {
        this.matchRepo = matchRepo;
        this.tournamentRepo = tournamentRepo;
        this.stageRepo = stageRepo;
        this.tournamentTeamRepo = tournamentTeamRepo;
    }

    // Створення матчів для турніру (робиться один раз при створенні турніру)
    public void createMatchesForTournament(Long tournamentId) {
        // З бази завантажується турнір по id
        Optional<Tournament> opTournament = tournamentRepo.findById(tournamentId);
        // Перевірка чи турнір існує
        if (opTournament.isPresent()) {
            // Присвоєння посилання на об'єкт з Optional у екземпляр класу Tournament
            Tournament tournament = opTournament.get();
            // Ініціалізація пустого списку матчів
            List<Match> matches = new ArrayList<>();
            // Отримання всі команд турніру з бази даних, упорядкованих за зростанням порядкового номера
            List<TournamentTeam> tournamentTeams = tournamentTeamRepo.findByTournamentIdOrderBySequentNumberAsc(tournamentId);
            // Перебір команд турніру (виконується з кроком 2 команди за турнір)
            for (int i = 0; i < tournamentTeams.size(); i++) {
                // У список матчів додається матч в який передається турнір, поточний етап турніру, а також перша команда та друга команда (порядок аргументів можна побачити у конструкторі класу Match)
                matches.add(new Match(tournament, tournament.getCurrentStage(), tournamentTeams.get(i), tournamentTeams.get(i+1)));
                // додаткова інкрементація індекса, так як було використано 2 команди з поточним та наступним індексом
                i++;
            }
            // Після генерації списку матчів - він зберігається у базі даних
            matchRepo.saveAll(matches);
        }
    }

    // Створення турнірів для наступної стадії (виконується кожного разу після вказання результату всіх матчів попередньї стадії)
    public void createNextStage(Long tournamentId) throws NotFoundException {
        // Отримання турніру з бази даних
        Optional<Tournament> opTournament = tournamentRepo.findById(tournamentId);
        // Перевірка чи турнір існує, і чи в турнірі ще немає переможця (якщо переможець є - турнір завершений і стадії генерувати не потрібно)
        if (opTournament.isPresent() && opTournament.get().getTournamentWinner() == null) {
            // Присвоєння об'єкту отриманого з бази даних у екземпляр класу Tournament
            Tournament tournament = opTournament.get();
            // Запис посилання на поточну стадію у екземпляр класу
            Stage currentStage = tournament.getCurrentStage();
            // Отримання всіх переможців поточної стадії по id турніру та поточній стадії
            List<TournamentTeam> stageWinners = matchRepo.findStageWinners(tournament, currentStage);
            // Перевірка чи переможців поточної стадії достатньо для початку наступної стадії турніру
            if (stageWinners.size() == currentStage.getRequiredTeamsCount() / 2) {
                // Встановлення наступної стадії у турнір яке виконується за допомогою кількості переможців
                // (у кожної стадії є кількість команд які потрібні для стадії)
                // якщо стадії для такої кількості нема - встановлюється значення null
                tournament.setCurrentStage(stageRepo.findByRequiredTeamsCount(currentStage.getRequiredTeamsCount() / 2).orElse(null));
                // Перевірка чи стадія встановилась, і чи нова стадія не є тою, на якій зараз турнір
                if (tournament.getCurrentStage() != null && tournament.getCurrentStage() != currentStage) {
                    // Створення пустого списку матчів наступної стадії
                    List<Match> nextStageMatches = new ArrayList<>();
                    // Прохід по всім переможцям попередньої стадії та створення для них матчів
                    for (int i = 0; i < stageWinners.size(); i++) {
                        nextStageMatches.add(new Match(tournament, tournament.getCurrentStage(), stageWinners.get(i), stageWinners.get(i+1)));
                        i++;
                    }
                    // Зберігання списку матчів до БД
                    matchRepo.saveAll(nextStageMatches);
                    // Оновлення змін, які було внесено у турнір
                    tournamentRepo.save(tournament);
                // Повертається при некоректній кількості гравців (при помилці пошуку етапу по кількості гравців)
                } else throw new NotFoundException("Wrong stage");
            }
        // Повертається якщо турнір вже має переможця
        } else throw new NotFoundException("This tournament already have winner");
    }

    // вибір переможця матчу та результату
    public void setMatchWinner(Long matchId, double firstTeamResult, double secondTeamResult) throws NotFoundException {
        // Вибірка матчу з бази даних по id
        Optional<Match> opMatch = matchRepo.findById(matchId);
        // Перевірка чи матч існує
        if (opMatch.isPresent()) {
            // Перевірка чи в матчі немає переможця
            if (opMatch.get().getWinner() == null) {
                // Присвоєння посилання на матч у екземпляр класу
                Match match = opMatch.get();
                // Перевірка на правильність результату матчу (чи рахунок не однаковий і чи рахунок не менше нуля)
                if (firstTeamResult != secondTeamResult && firstTeamResult > -1 && secondTeamResult > -1) {
                    // Визначення команди переможця матчу за допомогою порівняння
                    if (firstTeamResult > secondTeamResult) {
                        match.setWinner(match.getFirstTeam());
                    } else match.setWinner(match.getSecondTeam());

                    // Додавання результатів першої та другої команди
                    match.setFirstTeamResult(firstTeamResult);
                    match.setSecondTeamResult(secondTeamResult);
                    // Збереження даних матчу
                    matchRepo.save(match);


                    // Перевірка чи матч не був останнім у турнірі
                    if (match.getStage().getRequiredTeamsCount() == 2) {
                        // Якщо матч останній - потрібно зберегти переможця турніру, турнір вибирається з бази даних по id
                        Optional<Tournament> opTournament = tournamentRepo.findById(match.getTournament().getId());
                        // Перевірка чи турнір існує
                        if (opTournament.isPresent()) {
                            // Присвоєння посилання на турнір в екземпляр класу
                            Tournament tournament = opTournament.get();
                            // Вибирається переможець турніру по результатах останнього матчу
                            tournament.setTournamentWinner(match.getWinner().getTeam());
                            // Після зміни даних турнір оновлюється у БД
                            tournamentRepo.save(tournament);
                        }
                    } else {
                        // Якщо матч в турнірі не останній то створюється новий етап турніру (якщо кількість гравців буде правильна)
                        createNextStage(match.getTournament().getId());
                    }
                // Повертається якщо в параметрах приходить некоректний результат матчу
                } else throw new NotFoundException("Result is not valid");
            // Повертається якщо у матчі вже є переможець
            } else throw new NotFoundException("Match already have winner");
            // Повертається при некоректному id матчу
        } else throw new NotFoundException("Cannot find match with id " + matchId);
    }
}
