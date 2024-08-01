package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
public class MainController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private static final String REDIRECT = "redirect:/";

    @Autowired
    public MainController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String getUserProfile(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser, Authentication authentication) {
        User user = userService.findByUsername(currentUser.getUsername());
        model.addAttribute("userUsername", user.getUsername());
        model.addAttribute("userName", user.getName());
        model.addAttribute("userLastName", user.getLastName());
        model.addAttribute("userAge", user.getAge());
        model.addAttribute("curUsersRoles", authentication.getAuthorities().stream().map(r-> r.getAuthority()).toList());
        return "user";
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String printWelcome(ModelMap model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/admin/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addUserPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "userAdd";
    }

    @PostMapping("/admin/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addUser(@RequestParam String username, @RequestParam String password, @RequestParam String name, @RequestParam String lastName, @RequestParam int age, RedirectAttributes redirectAttributes) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setLastName(lastName);
        user.setAge(age);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message", "User added successfully");
        return REDIRECT;
    }

    @GetMapping("/admin/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editUser(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "edit";
        } else {
            return REDIRECT;
        }
    }

    @PostMapping("/admin/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User updatedUser) {
        User existingUser = userService.getUserById(id);
        existingUser.setName(updatedUser.getName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setAge(updatedUser.getAge());
        userService.saveUser(existingUser);
        return REDIRECT;
    }

    @GetMapping("/admin/delete/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return REDIRECT;
    }

}