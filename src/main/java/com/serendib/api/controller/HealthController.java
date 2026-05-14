package com.serendib.api.controller;

import com.serendib.api.entity.User;
import com.serendib.api.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    private final UserService userService;

    public HealthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck(){
        Map<String, Object> response = new HashMap<>();

        response.put("status", "UP");
        response.put("app", "Serendib AI API");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    // TEST endpoint: create a dummy user
    @PostMapping("/test/users")
    public User createTestUser() {
        return userService.createUser(
                "Chamela Yohan",
                "chamela@serendib.com",
                "password123"
        );
    }

    // TEST endpoint: get all users
    @GetMapping("/test/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
