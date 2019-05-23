package ua.tournament.grid.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import ua.tournament.grid.entities.Tournament;

import java.util.Optional;

public interface TournamentRepo extends PagingAndSortingRepository<Tournament, Long> {

    Optional<Tournament> findFirstById(Long id);

}
