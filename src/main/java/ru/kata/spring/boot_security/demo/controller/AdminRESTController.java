package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminRESTController {

    private final PasswordEncoder passwordEncoder;
    private UserService userService;

    @Autowired
    public AdminRESTController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> readUserById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<User>> readAllUsers() {
        Optional<List<User>> users = Optional.ofNullable(userService.findAll());
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody User newUser,
                                             @PathVariable("id") Long id) {
        Optional<User> currentUser = Optional.ofNullable(userService.getUserById(id));
        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        currentUser.get().setPassword(passwordEncoder.encode(newUser.getPassword()));
        currentUser.get().setRoles(newUser.getRoles());
        currentUser.get().setUsername(newUser.getUsername());
        currentUser.get().setAge(newUser.getAge());
        currentUser.get().setName(newUser.getName());
        currentUser.get().setLastName(newUser.getLastName());
        userService.saveUser(currentUser.get());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
