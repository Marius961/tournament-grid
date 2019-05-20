package ua.tournament.grid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tournament.grid.entities.User;
import ua.tournament.grid.exceptions.UserRegistrationFailedException;
import ua.tournament.grid.services.UserService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody User user) throws UserRegistrationFailedException {
        userService.createUser(user, passwordEncoder);
    }

    @PostMapping("/is-registered")
    public Map<String, Boolean> checkUsername(@RequestBody Map<String, String> payload) {
        return Collections.singletonMap("isRegistered", userService.isRegistered(payload.get("username")));
    }

}
