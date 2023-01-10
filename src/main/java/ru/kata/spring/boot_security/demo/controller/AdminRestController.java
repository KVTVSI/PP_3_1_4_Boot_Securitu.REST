package ru.kata.spring.boot_security.demo.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.handler.DataInfoHandler;
import ru.kata.spring.boot_security.demo.handler.UserWithSuchLoginExist;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;

    final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminRestController(UserService userService, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping
    public ResponseEntity<UserDetails> getAdminName(Principal principal) {
        return new ResponseEntity<>(userService.loadUserByUsername(principal.getName()), HttpStatus.OK);
    }
    @GetMapping("/usersList")
    public ResponseEntity<List> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/usersList/{id}")
    public ResponseEntity<User> getOneUser(@PathVariable("id") long id) {
        User user = userService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> addNewUser(@RequestBody User user) {
        if (userService.addUser(user)) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(user,HttpStatus.CONFLICT);
    }

    @PutMapping("/usersList/{id}")
    public ResponseEntity<DataInfoHandler> updateUser(@PathVariable("id") long id,
                                                      @RequestBody @Valid User user,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = getErrorsFromBindingResult(bindingResult);
            return new ResponseEntity<>(new DataInfoHandler(error), HttpStatus.BAD_REQUEST);
        }
        try {
            System.out.println(user.getPassword());
            userService.updateUser(user, user.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            throw new UserWithSuchLoginExist("User with such login Exist");
        }
    }

    @DeleteMapping("/usersList/{id}")
    public ResponseEntity<Long> deleteUser(@PathVariable long id) {
        if (userService.deleteUser(id)) {
            return new ResponseEntity<>(id, HttpStatus.OK);
        }
        return new ResponseEntity<>(id,HttpStatus.NOT_FOUND);
    }

    private String getErrorsFromBindingResult(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.joining("; "));
    }
}