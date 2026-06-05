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
public class TripStatusChangedEvent {
    private UUID tripId;
    private String tripTitle;
    private String userEmail;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime changedAt;
}