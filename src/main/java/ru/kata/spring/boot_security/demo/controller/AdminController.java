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
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/")
    public ModelAndView rootRedirect() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
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

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin");
        return modelAndView;
    }

    @PostMapping("/admin/update")
    public ModelAndView updateUser(@ModelAttribute User user,
                                   @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        if (roleIds == null) {
            roleIds = new HashSet<>();
        }
        user.setRoles(roleService.findByIds(roleIds));
        userService.update(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin");
        return modelAndView;
    }

    @PostMapping("/admin/delete")
    public ModelAndView deleteUser(@RequestParam("id") Long id) {
        userService.deleteById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin");
        return modelAndView;
    }
}