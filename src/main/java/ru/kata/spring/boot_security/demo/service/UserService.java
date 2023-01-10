package ru.kata.spring.boot_security.demo.service;




import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    boolean addUser(User user);

    void updateUser(User user, String password);

    User getUser(Long id);

    List<User> getAllUsers();

    boolean deleteUser(Long id);

}
