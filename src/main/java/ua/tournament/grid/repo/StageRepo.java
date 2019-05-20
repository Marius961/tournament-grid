package ua.tournament.grid.repo;

import org.springframework.data.repository.CrudRepository;
import ua.tournament.grid.entities.Stage;

public interface StageRepo extends CrudRepository<Stage, Long> {
}
