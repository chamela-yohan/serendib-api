package com.serendib.api.dto.request;

import com.serendib.api.entity.Trip;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreateTripRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    // Where they start in Sri Lanka
    @NotBlank(message = "Start location is required")
    private String startLocation;

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "10.0", message = "Minimum budget is $10")
    @DecimalMax(value = "50000.0", message = "Maximum budget is $50,000")
    private BigDecimal budgetUsd;

    @NotNull(message = "Number of days is required")
    @Min(value = 1, message = "Minimum 1 day")
    @Max(value = 30, message = "Maximum 30 days")
    private Integer numberOfDays;

    @NotNull(message = "Travel style is required")
    private Trip.TravelStyle travelStyle;

    @NotNull(message = "Group type is required")
    private Trip.GroupType groupType;

    @NotNull(message = "Adventure level is required")
    private Trip.AdventureLevel adventureLevel;

    // Optional fields — no @NotNull
    private List<String> foodPreferences;

    // Flexible JSON: { "heartCondition": true, "asthma": false }
    private Map<String, Object> healthConditions;
}