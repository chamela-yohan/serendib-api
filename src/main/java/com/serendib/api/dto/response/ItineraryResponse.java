package com.serendib.api.dto.response;

import com.serendib.api.entity.Itinerary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryResponse {

    private UUID id;
    private UUID tripId;
    private String tripTitle;
    private List<Itinerary.DayPlan> days;
    private BigDecimal estimatedTotalCost;
    private List<String> packingSuggestions;
    private List<String> generalTips;
    private String generatedBy;
    private LocalDateTime createdAt;
}