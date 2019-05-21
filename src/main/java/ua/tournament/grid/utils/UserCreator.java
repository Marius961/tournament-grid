package ua.tournament.grid.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.tournament.grid.entities.Role;
import ua.tournament.grid.entities.Stage;
import ua.tournament.grid.entities.User;
import ua.tournament.grid.repo.StageRepo;
import ua.tournament.grid.repo.UserRepo;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserCreator {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final StageRepo stageRepo;

    @Autowired
    public UserCreator(UserRepo userRepo, PasswordEncoder passwordEncoder, StageRepo stageRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.stageRepo = stageRepo;
    }

    @PostConstruct
    private void init() {
        addAdmin();
    }


    private void addAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setActive(true);
        admin.setRoles(new HashSet<>(Arrays.asList(Role.USER, Role.ADMIN)));
        userRepo.save(admin);

        Set<Stage> stageSet = new HashSet<>();
        stageSet.add(new Stage("Кваліфікації", "0", 64));
        stageSet.add(new Stage("Етап претендентів", "1", 32));
        stageSet.add(new Stage("Груповий етап","2", 16));
        stageSet.add(new Stage("Чверть фінал", "3", 8));
        stageSet.add(new Stage("напів-фінал", "4", 4));
        stageSet.add(new Stage("Фінал", "5",2));
        stageRepo.saveAll(stageSet);
    }
}