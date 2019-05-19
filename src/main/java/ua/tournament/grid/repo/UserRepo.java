package ua.tournament.grid.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ua.tournament.grid.entities.User;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT count(u) FROM User u WHERE u.email = :email AND  u.id <> :id")
    int countUserByEmailAndIdNot(String email, Long id);
}
