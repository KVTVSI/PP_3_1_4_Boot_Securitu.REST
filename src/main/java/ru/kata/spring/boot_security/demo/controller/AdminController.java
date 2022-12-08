package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@RequestMapping("/admin")
@Controller
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;



    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String getAllUsers(Model model, Principal principal) {
        model.addAttribute("admin", userService.loadUserByUsername(principal.getName()));
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAll());
        model.addAttribute("newUser", new User());
//        return "admin/userslist";
        return "/admin";
    }

    @PostMapping
    public String saveUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult,
                           @RequestParam(value = "allRoles", required = false) String roles) {
        try {
                Set<Role> roles1 = new HashSet<>();
                roles1.add(roleService.findRoleByName(roles));
                roles1.add(roleService.findRoleByName("ROLE_USER"));
                user.setRoles(roles1);
                userService.addUser(user);
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            user.setPassword("");
            e.printStackTrace();
            return "/admin";
        }
        return "redirect:/admin";
    }

    @PatchMapping("/{id}")
    public String updateUser(@ModelAttribute("newUser") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "allRoles", required = false) String roles,
                             @RequestParam(value = "password2", required = false) String password) {
        try {

                Set<Role> roles1 = new HashSet<>();
                roles1.add(roleService.findRoleByName(roles));
                roles1.add(roleService.findRoleByName("ROLE_USER"));
                user.setRoles(roles1);
            System.out.println("!" + password + "!");

                userService.updateUser(user, password);
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            e.printStackTrace();
            return "/admin";
        }
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
