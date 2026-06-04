package com.serendib.api.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SosResponse {
    private UUID alertId;
    private String status;
    private List<String> nationalEmergencyNumbers;
    private EmergencyServiceResponse nearestHospital;
    private EmergencyServiceResponse nearestPolice;
    private List<String> firstAidTips;
    private String message;
}