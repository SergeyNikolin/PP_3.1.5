package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);
    List<User> findAll();

    User findByUsername(String username);

    User getUserById(Long id);

    void deleteUser(Long userId);
}
