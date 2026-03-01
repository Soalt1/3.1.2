package ru.kata.spring.boot_security.demo;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initializeData(UserService userService, RoleService roleService) {
        return args -> {
            // Инициализируем роли
            roleService.initializeRoles();

            // Создаем тестовых пользователей, если их нет
            try {
                userService.findByUsername("admin");
            } catch (RuntimeException e) {
                // Создаем админа
                User admin = new User("admin", "admin", "admin@example.com", "Admin", "Adminov", 30);
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(roleService.findByName("ROLE_ADMIN"));
                adminRoles.add(roleService.findByName("ROLE_USER"));
                admin.setRoles(adminRoles);
                userService.save(admin);
            }

            try {
                userService.findByUsername("user");
            } catch (RuntimeException e) {
                // Создаем обычного пользователя
                User user = new User("user", "user", "user@example.com", "User", "Userov", 25);
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(roleService.findByName("ROLE_USER"));
                user.setRoles(userRoles);
                userService.save(user);
            }
        };
    }
}
