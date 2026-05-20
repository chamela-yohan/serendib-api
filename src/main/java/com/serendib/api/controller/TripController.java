package com.serendib.api.controller;

import com.serendib.api.common.ApiResponse;
import com.serendib.api.dto.request.CreateTripRequest;
import com.serendib.api.dto.response.TripResponse;
import com.serendib.api.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    // POST /api/v1/trips — create a new trip
    @PostMapping
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @RequestBody @Valid CreateTripRequest request) {

        TripResponse trip = tripService.createTrip(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip created successfully", trip));
    }

    // GET /api/v1/trips — get MY trips
    @GetMapping
    public ResponseEntity<ApiResponse<List<TripResponse>>> getMyTrips() {

        List<TripResponse> trips = tripService.getMyTrips();
        return ResponseEntity.ok(
                ApiResponse.success("Trips retrieved successfully", trips)
        );
    }

    // GET /api/v1/trips/{id} — get single trip
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(
            @PathVariable UUID id) {

        TripResponse trip = tripService.getTripById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Trip retrieved successfully", trip)
        );
    }

    // DELETE /api/v1/trips/{id} — delete a trip
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @PathVariable UUID id) {

        tripService.deleteTrip(id);
        return ResponseEntity.ok(
                ApiResponse.success("Trip deleted successfully")
        );
    }
}