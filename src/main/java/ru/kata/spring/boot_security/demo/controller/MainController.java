package ru.kata.spring.boot_security.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/")
public class MainController {

    //---------------<<LOGIN PAGE>>---------------//

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    //---------------<<USER INFO PAGE>>---------------//

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String showUserPage() {
        return "user";
    }

    //---------------<<ADMIN PAGE WITH USERS TABLE>>---------------//

    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String printWelcome() {
        return "admin";
    }

}