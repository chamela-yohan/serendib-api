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
public class SosTriggeredEvent {
    private UUID alertId;
    private String userEmail;
    private Double latitude;
    private Double longitude;
    private String message;
    private String nearestHospital;
    private String nearestPolice;
    private LocalDateTime triggeredAt;
}