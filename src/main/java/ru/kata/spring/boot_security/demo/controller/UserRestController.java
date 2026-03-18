package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }
}
