package com.serendib.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serendib.api.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// This fires when someone hits a protected route with NO token
// Without this — Spring returns empty 403
// With this — return our clean ApiResponse JSON
@Component
@RequiredArgsConstructor
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // Spring provides this — converts to JSON

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.error(
                "Access denied. Please login first."
        );

        // Write JSON to response
        response.getWriter()
                .write(objectMapper.writeValueAsString(body));
    }
}