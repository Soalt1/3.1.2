package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final RoleService roleService;

    public AdminServiceImpl(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public ModelAndView prepareAdminPanel(User currentUser) {
        ModelAndView modelAndView = new ModelAndView("admin");
        modelAndView.addObject("users", userService.findAll());
        modelAndView.addObject("roles", roleService.findAll());
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }

    @Override
    @Transactional
    public void addUser(String firstName, String lastName, Integer age,
                        String email, String password, Set<Long> roleIds) {
        User user = new User(firstName, lastName, age, email, password);

        Set<Role> roles = (roleIds == null || roleIds.isEmpty())
                ? new HashSet<>()
                : roleService.findByIds(roleIds);
        user.setRoles(roles);

        userService.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, String firstName, String lastName, Integer age,
                           String email, String password, Set<Long> roleIds) {
        User user = userService.findById(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }

        Set<Role> roles = (roleIds == null || roleIds.isEmpty())
                ? new HashSet<>()
                : roleService.findByIds(roleIds);
        user.setRoles(roles);

        userService.update(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userService.deleteById(id);
    }
}