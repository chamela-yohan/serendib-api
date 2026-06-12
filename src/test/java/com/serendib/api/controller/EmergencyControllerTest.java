package com.serendib.api.controller;

import com.serendib.api.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class EmergencyControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Helper: register and get token
    private String getToken() throws Exception {
        // Unique email per call to avoid duplicate registration across test runs
        String email = "emergency_" + System.currentTimeMillis() + "@serendib.com";
        var result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "name": "Emergency Tester",
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(email)))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        int idx = body.indexOf("\"token\":\"") + 9;
        return body.substring(idx, body.indexOf("\"", idx));
    }

    @Test
    void shouldReturnNationalEmergencyContactsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/emergency/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("Police: 119"));
    }

    @Test
    void shouldFindNearbyHospitals() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/emergency/nearby")
                        .param("lat", "6.9271")
                        .param("lng", "79.8612")
                        .param("type", "HOSPITAL")
                        .param("limit", "3")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("HOSPITAL"))
                .andExpect(jsonPath("$.data[0].phone").exists());
    }

    @Test
    void shouldFindServicesByProvince() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/emergency/services")
                        .param("province", "Central")
                        .param("type", "POLICE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldTriggerSosWithLocation() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/v1/emergency/sos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                    {
                        "latitude": 7.2906,
                        "longitude": 80.6337,
                        "message": "Test SOS from integration test"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.alertId").exists())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.nationalEmergencyNumbers").isArray())
                .andExpect(jsonPath("$.data.nearestHospital").exists())
                .andExpect(jsonPath("$.data.nearestPolice").exists())
                .andExpect(jsonPath("$.data.firstAidTips").isArray());
    }

    @Test
    void shouldRejectNearbyWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/emergency/nearby")
                        .param("lat", "6.9271")
                        .param("lng", "79.8612"))
                .andExpect(status().isUnauthorized());
    }
}