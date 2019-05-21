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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepo matchRepo;
    private final TournamentRepo tournamentRepo;
    private final StageRepo stageRepo;

    @Autowired
    public MatchService(MatchRepo matchRepo, TournamentRepo tournamentRepo, StageRepo stageRepo) {
        this.matchRepo = matchRepo;
        this.tournamentRepo = tournamentRepo;
        this.stageRepo = stageRepo;
    }

    public void createMatchesForTournament(Long tournamentId) {
        Optional<Tournament> opTournament = tournamentRepo.findById(tournamentId);
        if (opTournament.isPresent()) {
            Tournament tournament = opTournament.get();
            List<Match> matches = new ArrayList<>();
            List<TournamentTeam> tournamentTeams = new ArrayList<>(tournament.getTournamentTeams());
            for (int i = 0; i < tournament.getTournamentTeams().size(); i++) {
                matches.add(new Match(tournament, tournament.getCurrentStage(), tournamentTeams.get(i), tournamentTeams.get(i++)));
            }
            matchRepo.saveAll(matches);
        }
    }

    public void createNextStage(Long tournamentId) throws NotFoundException {
        Optional<Tournament> opTournament = tournamentRepo.findById(tournamentId);
        if (opTournament.isPresent()) {
            Tournament tournament = opTournament.get();
            List<TournamentTeam> stageWinners = matchRepo.findStageWinners(tournament, tournament.getCurrentStage());
            Stage currentStage = tournament.getCurrentStage();
            if (stageWinners.size() == currentStage.getRequiredTeamsCount() / 2) {
                switch (tournament.getCurrentStage().getCode()) {
                    case ("0"): tournament.setCurrentStage(stageRepo.findByCode("1").orElse(null));
                    case ("1"): tournament.setCurrentStage(stageRepo.findByCode("2").orElse(null));
                    case ("2"): tournament.setCurrentStage(stageRepo.findByCode("3").orElse(null));
                    case ("3"): tournament.setCurrentStage(stageRepo.findByCode("4").orElse(null));
                    case ("4"): tournament.setCurrentStage(stageRepo.findByCode("5").orElse(null));
                }
                if (tournament.getCurrentStage() != null && tournament.getCurrentStage() != currentStage) {
                    List<Match> nextStageMatches = new ArrayList<>();
                    for (int i = 0; i < stageWinners.size(); i++) {
                        nextStageMatches.add(new Match(tournament, tournament.getCurrentStage(), stageWinners.get(i), stageWinners.get(i++)));
                    }
                    matchRepo.saveAll(nextStageMatches);
                    tournamentRepo.save(tournament);
                } else throw new NotFoundException("Wrong stage");
            } else throw new NotFoundException("Unhandled");
        }
    }

    public void setMatchWinner(Long matchId, double firstTeamResult, double secondTeamResult) throws NotFoundException {
        Optional<Match> opMatch = matchRepo.findById(matchId);
        if (opMatch.isPresent()) {
            if (opMatch.get().getWinner() == null) {
                Match match = opMatch.get();
                if (firstTeamResult > secondTeamResult) {
                    match.setWinner(match.getFirstTeam());
                } else match.setWinner(match.getSecondTeam());

                match.setFirstTeamResult(firstTeamResult);
                match.setSecondTeamResult(secondTeamResult);
                matchRepo.save(match);
                createNextStage(match.getTournament().getId());
            } else throw new NotFoundException("Match already have winner");
        } else throw new NotFoundException("Cannot find match with id " + matchId);
    }
}