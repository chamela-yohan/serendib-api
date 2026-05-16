package com.serendib.api.controller;

import com.serendib.api.common.ApiResponse;
import com.serendib.api.dto.CreateUserRequest;
import com.serendib.api.dto.UserResponse;
import com.serendib.api.entity.User;
import com.serendib.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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

    // TEST endpoint: create a user (new uses DTO + validation)
    @PostMapping("/test/users")
    public ApiResponse<UserResponse> createTestUser(
            @RequestBody @Valid CreateUserRequest request
            ) {
        UserResponse user = userService.createUser(request);
        return ApiResponse.success("User created successfully", user);
    }

    // TEST endpoint: get all users
    @GetMapping("/test/users")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ApiResponse.success("Users retrieved successfully", users);
    }
}
