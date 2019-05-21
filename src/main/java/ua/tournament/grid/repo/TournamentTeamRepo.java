package ua.tournament.grid.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ua.tournament.grid.entities.TournamentTeam;

import java.util.List;

public interface TournamentTeamRepo extends PagingAndSortingRepository<TournamentTeam, Long> {


    List<TournamentTeam> findByTournamentIdOrderBySequentNumberAsc(Long tournamentId);
}
