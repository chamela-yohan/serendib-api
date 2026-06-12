package com.serendib.api.controller;

import com.serendib.api.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class TripControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Register a unique user per test run
        String email = "trip_" + System.currentTimeMillis() + "@serendib.com";
        var result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "Trip Tester",
                        "email": "%s",
                        "password": "password123"
                    }
                    """.formatted(email)))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        int idx = body.indexOf("\"token\":\"") + 9;
        token = body.substring(idx, body.indexOf("\"", idx));
    }

    @Test
    void shouldCreateTripSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                    {
                        "title": "Kandy Cultural Tour",
                        "startLocation": "Colombo",
                        "numberOfDays": 3,
                        "budgetUsd": 500,
                        "travelStyle": "COMFORT",
                        "groupType": "COUPLE",
                        "adventureLevel": "HIGH"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Kandy Cultural Tour"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void shouldGetMyTrips() throws Exception {
        // Create a trip first
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                    {
                        "title": "Galle Fort Visit",
                        "startLocation": "Colombo",
                        "numberOfDays": 2,
                        "budgetUsd": 300,
                        "travelStyle": "BUDGET",
                        "groupType": "SOLO",
                        "adventureLevel": "LOW"
                    }
                    """))
                .andExpect(status().isCreated());

        // Get my trips
        mockMvc.perform(get("/api/v1/trips")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForOtherUserTrip() throws Exception {
        // Register second user
        var result2 = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "Other User",
                        "email": "other_%d@serendib.com",
                        "password": "password123"
                    }
                    """.formatted(System.currentTimeMillis())))
                .andReturn();

        String body2 = result2.getResponse().getContentAsString();
        int idx2 = body2.indexOf("\"token\":\"") + 9;
        String otherToken = body2.substring(idx2, body2.indexOf("\"", idx2));

        // Create trip as first user
        var tripResult = mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                    {
                        "title": "My Private Trip",
                        "startLocation": "Colombo",
                        "numberOfDays": 1,
                        "budgetUsd": 100,
                        "travelStyle": "BUDGET",
                        "groupType": "SOLO",
                        "adventureLevel": "LOW"
                    }
                    """))
                .andReturn();

        String tripBody = tripResult.getResponse().getContentAsString();
        int idIdx = tripBody.indexOf("\"id\":\"") + 6;
        String tripId = tripBody.substring(idIdx, tripBody.indexOf("\"", idIdx));

        // Try to access as second user — should 404
        mockMvc.perform(get("/api/v1/trips/" + tripId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
    }
}