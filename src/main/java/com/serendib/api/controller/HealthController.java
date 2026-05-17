package com.serendib.api.controller;

import com.serendib.api.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> healthCheck() {
        return ApiResponse.success("API is running", Map.of(
                "app",       "Serendib AI API",
                "version",   "1.0.0",
                "timestamp", LocalDateTime.now().toString(),
                "status",    "UP"
        ));
    }

    @GetMapping("/greet/{name}")
    public ApiResponse<Map<String, String>> greet(@PathVariable String name) {
        return ApiResponse.success("Greeting generated", Map.of(
                "message", "Welcome to Serendib AI, " + name + "!",
                "hint",    "Your Sri Lanka adventure starts here."
        ));
    }
}