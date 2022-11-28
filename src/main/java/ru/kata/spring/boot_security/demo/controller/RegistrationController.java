package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.Collections;

@Controller
public class RegistrationController {

    final UserService userService;
    final RoleService roleService;

    final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegistrationController(UserService userService, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("newUser", new User());
        return "/registration";
    }

    @PostMapping("/registration")
    public String registrUser(@ModelAttribute("newUser") @Valid User newUser,
                              BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return "registration";
            } else {
                newUser.setRole(Collections.singleton(roleService.findRoleByName("ROLE_USER")));
                newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
                userService.addUser(newUser);
            }
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            return "registration";
        }

        return "redirect:/";
    }
}
