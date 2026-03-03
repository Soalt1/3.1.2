package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.util.Set;

@Controller
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }

    @GetMapping("/")
    public ModelAndView rootRedirect() {
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/admin")
    public ModelAndView adminPanel(@AuthenticationPrincipal User currentUser) {
        ModelAndView modelAndView = new ModelAndView("admin");
        modelAndView.addObject("users", userService.findAll());
        modelAndView.addObject("roles", roleService.findAll());
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }

    @PostMapping("/admin/add")
    public ModelAndView addUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("age") Integer age,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {

        userService.createUser(firstName, lastName, age, email, password, roleIds);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/admin/update")
    public ModelAndView updateUser(
            @RequestParam("id") Long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("age") Integer age,
            @RequestParam("email") String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {

        userService.updateUser(id, firstName, lastName, age, email, password, roleIds);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/admin/delete")
    public ModelAndView deleteUser(@RequestParam("id") Long id) {
        userService.deleteById(id);
        return new ModelAndView("redirect:/admin");
    }
}