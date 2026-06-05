package com.serendib.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryGeneratedEvent {
    private UUID itineraryId;
    private UUID tripId;
    private String tripTitle;
    private String userEmail;
    private int numberOfDays;
    private LocalDateTime generatedAt;
}