package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RequestMapping("/admin")
@Controller
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public AdminController(UserService userService, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping()
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/userslist";
    }

    @GetMapping("/new")
    public String addNewUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAll());
        return "admin/new";
    }

    @PostMapping
    public String saveUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult,
                           @RequestParam(value = "allRoles", required = false) String roles) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/new";
            } else {
                Set<Role> roles1 = new HashSet<>();
                roles1.add(roleService.findRoleByName(roles));
                roles1.add(roleService.findRoleByName("ROLE_USER"));
                user.setRole(roles1);
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                userService.addUser(user);
            }
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            user.setPassword("");
            e.printStackTrace();
            return "admin/new";
        }
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editUser(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.getUser(id));
        model.addAttribute("allRoles", roleService.getAll());

        return "admin/edit";
    }

    @PatchMapping("/{id}")
    public String updateUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "allRoles", required = false) String roles) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/edit";
            } else {
                Set<Role> roles1 = new HashSet<>();
                roles1.add(roleService.findRoleByName(roles));
                roles1.add(roleService.findRoleByName("ROLE_USER"));
                user.setRole(roles1);
                userService.addUser(user);
            }
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            e.printStackTrace();
            return "admin/edit";
        }
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
