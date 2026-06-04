package com.serendib.api.dto.request;

import lombok.Data;

@Data
public class SosRequest {
    private Double latitude;
    private Double longitude;
    private String message;  // optional — describe the emergency
}