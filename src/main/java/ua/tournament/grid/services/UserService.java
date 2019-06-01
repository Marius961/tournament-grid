package ua.tournament.grid.services;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.tournament.grid.entities.Role;
import ua.tournament.grid.entities.User;
import ua.tournament.grid.exceptions.UserRegistrationFailedException;
import ua.tournament.grid.repo.UserRepo;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> opUser = userRepo.findByUsername(s);
        if (!opUser.isPresent()) {
            throw new UsernameNotFoundException(s);
        }
        return opUser.get();
    }

    public void createUser(User user, PasswordEncoder passwordEncoder) throws UserRegistrationFailedException {
        if (!userRepo.existsByUsername(user.getUsername())) {
            if (user.getPassword().length() <= 16) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRoles(Collections.singleton(Role.USER));
                userRepo.save(user);
            } else throw new IllegalArgumentException("Maximum password length is 16  characters");
        } else throw new UserRegistrationFailedException("Failed to register new user, because username or email already exist.");
    }

    public boolean isRegistered(String username) throws HibernateException {
        return userRepo.existsByUsername(username);
    }

    public User getAccountIbfo() {
        return (User) loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public void changePassword(String oldPassword, String newPassword, PasswordEncoder passwordEncoder) {
        User currentUser = (User) loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (currentUser != null && oldPassword != null) {
            if (BCrypt.checkpw(oldPassword, currentUser.getPassword())) {
                if (newPassword != null && newPassword.length() >= 6 && newPassword.length() <= 16) {
                    currentUser.setPassword(passwordEncoder.encode(newPassword));
                    userRepo.save(currentUser);
                } else throw new IllegalArgumentException("Invalid new password");
            } else throw new IllegalArgumentException("Passwords do not match");
        } else throw new UsernameNotFoundException("Cannot find user");
    }
}
