package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.service.AdminService;

import java.util.Set;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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
        return adminService.prepareAdminPanel(currentUser);
    }

    @PostMapping("/admin/add")
    public ModelAndView addUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("age") Integer age,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {

        adminService.addUser(firstName, lastName, age, email, password, roleIds);
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

        adminService.updateUser(id, firstName, lastName, age, email, password, roleIds);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/admin/delete")
    public ModelAndView deleteUser(@RequestParam("id") Long id) {
        adminService.deleteUser(id);
        return new ModelAndView("redirect:/admin");
    }
}