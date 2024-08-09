package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserRESTController {

    private UserService userService;

    @Autowired
    public UserRESTController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<User> showUserInfo(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        Optional<User> modelUser = Optional.ofNullable(userService.findByUsername(user.getUsername()));
        if (modelUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelUser.get());
    }
}
