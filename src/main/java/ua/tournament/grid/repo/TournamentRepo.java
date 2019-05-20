package ua.tournament.grid.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import ua.tournament.grid.entities.Tournament;

public interface TournamentRepo extends PagingAndSortingRepository<Tournament, Long> {
}
