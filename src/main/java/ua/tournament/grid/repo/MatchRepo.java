package ua.tournament.grid.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ua.tournament.grid.entities.Match;
import ua.tournament.grid.entities.Stage;
import ua.tournament.grid.entities.Tournament;
import ua.tournament.grid.entities.TournamentTeam;

import java.util.List;
import java.util.Optional;

public interface MatchRepo extends PagingAndSortingRepository<Match, Long> {

    List<Match> findAllByTournamentAndStage(Tournament tournament, Stage stage);

    @Query("SELECT m.winner FROM Match m WHERE m.tournament = :tournament AND m.stage = :stage order by m.winner.sequentNumber ASC ")
    List<TournamentTeam> findStageWinners(Tournament tournament, Stage stage);

}
