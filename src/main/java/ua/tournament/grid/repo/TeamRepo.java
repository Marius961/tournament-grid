package ua.tournament.grid.repo;

import org.springframework.data.repository.CrudRepository;
import ua.tournament.grid.entities.Team;

import java.util.Optional;

public interface TeamRepo extends CrudRepository<Team, Long> {

    Optional<Team> findByName(String name);
}
