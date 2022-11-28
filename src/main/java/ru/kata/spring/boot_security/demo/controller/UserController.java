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
import java.security.Principal;
import java.util.Collections;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserService userService, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping
    public String userInfo(Model model, Principal principal) {
        model.addAttribute("user", userService.loadUserByUsername(principal.getName()));
        return "user/userInfo";
    }

    @GetMapping("/edit")
    public String userEdit(Model model, Principal principal) {
        model.addAttribute("user", userService.loadUserByUsername(principal.getName()));
        model.addAttribute("allRoles", roleService.getAll());
        model.addAttribute("userPassword", userService.loadUserByUsername(principal.getName()).getPassword());
        return "user/edit";
    }

    @PatchMapping("/edit")
    public String userUpdate(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/edit";
            } else {
                user.setRole(Collections.singleton(roleService.findRoleByName("ROLE_USER")));
                userService.addUser(user);
            }
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            e.printStackTrace();
            return "user/edit";
        }
        return "redirect:/user";
    }

    @GetMapping("/edit/changePassword")
    public String userPassword(Model model, Principal principal) {
        model.addAttribute("user", userService.loadUserByUsername(principal.getName()));
        model.addAttribute("userPassword", userService.loadUserByUsername(principal.getName()).getPassword());
        System.out.println(userService.loadUserByUsername(principal.getName()));
        return "user/changePassword";
    }

    @PatchMapping("/edit/changePassword")
    public String changePassword(@ModelAttribute("user") @Valid User user,
                                 BindingResult bindingResult,
                                 @ModelAttribute("userPassword") String userPassword,
                                 @RequestParam(value = "newPassword") String newPassword,
                                 @RequestParam(value = "oldPassword") String oldPassword) {
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {

            return "user/changePassword";
        }
        if(bindingResult.hasErrors()) {
            return "user/changePassword";
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setRole(Collections.singleton(roleService.findRoleByName("ROLE_USER")));
        userService.addUser(user);
        return "redirect:/user";
    }


}
