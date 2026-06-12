package com.serendib.api.controller;

import com.serendib.api.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AuthControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "Test User",
                        "email": "test@serendib.com",
                        "password": "password123"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user.email").value("test@serendib.com"));
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        // Register once
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "User One",
                        "email": "duplicate@serendib.com",
                        "password": "password123"
                    }
                    """))
                .andExpect(status().isCreated());

        // Register again with same email
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "User Two",
                        "email": "duplicate@serendib.com",
                        "password": "password456"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        // Register first
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "Login User",
                        "email": "login@serendib.com",
                        "password": "mypassword"
                    }
                    """))
                .andExpect(status().isCreated());

        // Login
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "email": "login@serendib.com",
                        "password": "mypassword"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void shouldRejectInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "email": "nonexistent@serendib.com",
                        "password": "wrongpass"
                    }
                    """))
                .andExpect(status().isUnauthorized());
    }
}