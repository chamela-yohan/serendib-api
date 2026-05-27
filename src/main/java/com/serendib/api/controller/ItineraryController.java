package com.serendib.api.controller;

import com.serendib.api.common.ApiResponse;
import com.serendib.api.dto.response.ItineraryResponse;
import com.serendib.api.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/itinerary")
@RequiredArgsConstructor
public class ItineraryController {

    private final ItineraryService itineraryService;

    // POST /api/v1/trips/{tripId}/itinerary/generate
    // Trigger AI generation
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<ItineraryResponse>> generate(
            @PathVariable UUID tripId) {

        ItineraryResponse itinerary =
                itineraryService.generateItinerary(tripId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Itinerary generated successfully! 🌴",
                        itinerary
                ));
    }

    // GET /api/v1/trips/{tripId}/itinerary
    // Get existing itinerary
    @GetMapping
    public ResponseEntity<ApiResponse<ItineraryResponse>> getItinerary(
            @PathVariable UUID tripId) {

        ItineraryResponse itinerary =
                itineraryService.getItinerary(tripId);

        return ResponseEntity.ok(
                ApiResponse.success("Itinerary retrieved successfully", itinerary)
        );
    }
}