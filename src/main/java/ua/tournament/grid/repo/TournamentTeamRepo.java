package ua.tournament.grid.repo;

import org.springframework.data.repository.CrudRepository;
import ua.tournament.grid.entities.TournamentTeam;

public interface TournamentTeamRepo extends CrudRepository<TournamentTeam, Long> {
}
