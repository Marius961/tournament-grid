package ua.tournament.grid.repo;

import org.springframework.data.repository.CrudRepository;
import ua.tournament.grid.entities.Team;

public interface TeamRepo extends CrudRepository<Team, Long> {
}
