package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.web.servlet.ModelAndView;
import java.util.Set;

public interface AdminService {
    ModelAndView prepareAdminPanel(User currentUser);
    void addUser(String firstName, String lastName, Integer age, String email,
                 String password, Set<Long> roleIds);
    void updateUser(Long id, String firstName, String lastName, Integer age,
                    String email, String password, Set<Long> roleIds);
    void deleteUser(Long id);
}