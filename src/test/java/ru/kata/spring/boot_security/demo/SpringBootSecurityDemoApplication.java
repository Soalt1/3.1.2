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
                userService.findByEmail("admin@mail.ru");
            } catch (RuntimeException e) {
                // Создаем админа
                User admin = new User("admin", "admin", 35, "admin@mail.ru", "admin");
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(roleService.findByName("ROLE_ADMIN"));
                adminRoles.add(roleService.findByName("ROLE_USER"));
                admin.setRoles(adminRoles);
                userService.save(admin);
            }

            try {
                userService.findByEmail("user@mail.ru");
            } catch (RuntimeException e) {
                // Создаем обычного пользователя
                User user = new User("user", "user", 30, "user@mail.ru", "user");
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(roleService.findByName("ROLE_USER"));
                user.setRoles(userRoles);
                userService.save(user);
            }
        };
    }
}
