package ua.tournament.grid.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.tournament.grid.entities.Team;
import ua.tournament.grid.entities.Tournament;
import ua.tournament.grid.entities.TournamentTeam;
import ua.tournament.grid.exceptions.NotFoundException;
import ua.tournament.grid.repo.TeamRepo;
import ua.tournament.grid.repo.TournamentRepo;
import ua.tournament.grid.repo.TournamentTeamRepo;

import java.util.*;

@Service
public class TournamentService {

    private final TournamentRepo tournamentRepo;
    private final TeamRepo teamRepo;
    private final TournamentTeamRepo tournamentTeamRepo;

    @Autowired
    public TournamentService(TournamentRepo tournamentRepo, TeamRepo teamRepo, TournamentTeamRepo tournamentTeamRepo) {
        this.tournamentRepo = tournamentRepo;
        this.teamRepo = teamRepo;
        this.tournamentTeamRepo = tournamentTeamRepo;
    }

    public void createTournament(Tournament tournament) throws NotFoundException {
        int teamsCount = tournament.getTournamentTeams().size();
        if (teamsCount == 2 || teamsCount == 4 || teamsCount == 8 || teamsCount == 16 || teamsCount == 32 || teamsCount == 64) {
            Set<TournamentTeam> verifiedTournamentTeams = new HashSet<>();

            List<Integer> sequentNumbers = getShuffledNumbersArray(tournament.getTournamentTeams().size());

            int index = 0;
            for (TournamentTeam tournamentTeam : tournament.getTournamentTeams()) {
                Optional<Team> opTeam = teamRepo.findByName(tournamentTeam.getTeam().getName());
                tournamentTeam.setTournament(tournament);
                tournamentTeam.setSequentNumber(sequentNumbers.get(index));
                index++;
                if (opTeam.isPresent()) {
                    tournamentTeam.setTeam(opTeam.get());
                    verifiedTournamentTeams.add(tournamentTeam);
                } else {
                    teamRepo.save(tournamentTeam.getTeam());
                    verifiedTournamentTeams.add(tournamentTeam);
                }
            }
            if (teamsCount == verifiedTournamentTeams.size()) {
                tournament.setId(null);
                tournamentRepo.save(tournament);
                tournamentTeamRepo.saveAll(verifiedTournamentTeams);
            } else throw new NotFoundException("Can't add two teams with the same names to tournament");
        } else throw new NotFoundException("Count of teams in tournament must be 2, 4, 8, 16, 32 or 64. In your tournament " + teamsCount + " teams");
    }

    private List<Integer> getShuffledNumbersArray(int maxNumber) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < maxNumber; i++) {
            list.add(i+1);
        }
        Collections.shuffle(list);
        return list;
    }

    public Page<Tournament> getAllTournaments(int page) {
        return tournamentRepo.findAll(PageRequest.of(page, 16));
    }
}
