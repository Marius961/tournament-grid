package ua.tournament.grid.repo;

import org.springframework.data.repository.CrudRepository;
import ua.tournament.grid.entities.Stage;

import java.util.Optional;

public interface StageRepo extends CrudRepository<Stage, Long> {

    Optional<Stage> findByRequiredTeamsCount(int teamsCount);

    Optional<Stage> findByCode(String code);
}
