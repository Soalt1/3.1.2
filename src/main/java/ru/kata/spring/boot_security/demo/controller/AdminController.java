package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashSet;
import java.util.Set;

@Controller
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
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
    public ModelAndView addUser(@ModelAttribute User user,
                                @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        if (roleIds == null) {
            roleIds = new HashSet<>();
        }
        user.setRoles(roleService.findByIds(roleIds));
        userService.save(user);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/admin/update")
    public ModelAndView updateUser(@ModelAttribute User user,
                                   @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        if (roleIds == null) {
            roleIds = new HashSet<>();
        }
        user.setRoles(roleService.findByIds(roleIds));
        userService.update(user);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/admin/delete")
    public ModelAndView deleteUser(@RequestParam("id") Long id) {
        userService.deleteById(id);
        return new ModelAndView("redirect:/admin");
    }
}