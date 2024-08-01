package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UsersRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {

    UsersRepository usersRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, RoleRepository roleRepository) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public void saveUser(User user) {
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName("ROLE_USER"));
        user.setRoles(roles);
        usersRepository.save(user);
    }

    @Transactional
    @Override
    public List<User> findAll() {
        return usersRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public User getUserById(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteUser(Long userId) {
        usersRepository.deleteById(userId);
    }
}
