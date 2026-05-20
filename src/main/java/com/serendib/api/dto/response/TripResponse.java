package com.serendib.api.dto.response;

import com.serendib.api.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {

    private UUID id;
    private String title;
    private String startLocation;
    private BigDecimal budgetUsd;
    private Integer numberOfDays;
    private Trip.TravelStyle travelStyle;
    private Trip.GroupType groupType;
    private Trip.AdventureLevel adventureLevel;
    private Trip.TripStatus status;
    private List<String> foodPreferences;
    private Map<String, Object> healthConditions;
    private String aiSummary;
    private LocalDateTime createdAt;

    // Include basic user info — not the full User entity
    // Prevents accidentally exposing password etc.
    private UUID userId;
    private String userName;
}