package com.serendib.api.controller;

import com.serendib.api.common.ApiResponse;
import com.serendib.api.dto.request.SosRequest;
import com.serendib.api.dto.response.EmergencyServiceResponse;
import com.serendib.api.dto.response.SosResponse;
import com.serendib.api.service.EmergencyAssistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyAssistanceService emergencyService;

    // GET /api/v1/emergency/contacts
    // Returns national emergency numbers — no auth needed
    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<List<String>>> getEmergencyContacts() {
        return ResponseEntity.ok(
                ApiResponse.success("Emergency numbers successfully retrieved",emergencyService.getNationalEmergencyNumbers())
        );
    }

    // GET /api/v1/emergency/nearby?lat=6.9271&lng=79.8612&type=HOSPITAL&limit=5
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<EmergencyServiceResponse>>> findNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "5") int limit) {

        return ResponseEntity.ok(
                ApiResponse.success("Nearby emergency places successfully retrieved",emergencyService.findNearby(lat, lng, type, limit))
        );
    }

    // GET /api/v1/emergency/services?province=Central&type=HOSPITAL
    @GetMapping("/services")
    public ResponseEntity<ApiResponse<List<EmergencyServiceResponse>>> findByProvince(
            @RequestParam String province,
            @RequestParam(required = false) String type) {

        return ResponseEntity.ok(
                ApiResponse.success("Province successfully retrieved",emergencyService.findByProvince(province, type))
        );
    }

    // POST /api/v1/emergency/sos
    @PostMapping("/sos")
    public ResponseEntity<ApiResponse<SosResponse>> triggerSos(
            @RequestBody SosRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("SOS triggered successfully",emergencyService.triggerSos(request))
        );
    }
}