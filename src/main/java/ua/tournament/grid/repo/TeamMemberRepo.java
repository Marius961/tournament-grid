package ua.tournament.grid.repo;

import org.springframework.data.repository.CrudRepository;
import ua.tournament.grid.entities.TeamMember;

public interface TeamMemberRepo extends CrudRepository<TeamMember, Long> {
}
