package com.serendib.api.dto.response;

import com.serendib.api.entity.EmergencyService;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class EmergencyServiceResponse {

    private UUID id;
    private String name;
    private String type;
    private String address;
    private String province;
    private String district;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String phone2;
    private boolean is24h;
    private String notes;
    private Double distanceKm;   // only present in nearby search

    public static EmergencyServiceResponse from(EmergencyService e) {
        return EmergencyServiceResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .type(e.getType())
                .address(e.getAddress())
                .province(e.getProvince())
                .district(e.getDistrict())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .phone(e.getPhone())
                .phone2(e.getPhone2())
                .is24h(e.getIs24h())
                .notes(e.getNotes())
                .build();
    }
}