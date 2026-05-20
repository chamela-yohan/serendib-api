package com.serendib.api.service;

import com.serendib.api.common.AuthUtils;
import com.serendib.api.dto.request.CreateTripRequest;
import com.serendib.api.dto.response.TripResponse;
import com.serendib.api.entity.Trip;
import com.serendib.api.entity.User;
import com.serendib.api.exception.ResourceNotFoundException;
import com.serendib.api.exception.BusinessException;
import com.serendib.api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
    private final AuthUtils authUtils;  // our helper to get current user

    // CREATE a new trip
    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {

        // Get the currently logged-in user
        User currentUser = authUtils.getCurrentUser();

        log.info("Creating trip for user: {}", currentUser.getEmail());

        // Build Trip entity from request DTO
        Trip trip = Trip.builder()
                .user(currentUser)                          // link to user
                .title(request.getTitle())
                .startLocation(request.getStartLocation())
                .budgetUsd(request.getBudgetUsd())
                .numberOfDays(request.getNumberOfDays())
                .travelStyle(request.getTravelStyle())
                .groupType(request.getGroupType())
                .adventureLevel(request.getAdventureLevel())
                .foodPreferences(request.getFoodPreferences())
                .healthConditions(request.getHealthConditions())
                .status(Trip.TripStatus.DRAFT) // always starts as DRAFT
                .build();

        // Save to DB
        Trip savedTrip = tripRepository.save(trip);

        log.info("Trip created with id: {}", savedTrip.getId());

        // Return DTO
        return mapToResponse(savedTrip);
    }

    // GET all trips for current user
    @Transactional
    public List<TripResponse> getMyTrips() {
        UUID userId = authUtils.getCurrentUserId();

        return tripRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET single trip by ID (only if it belongs to current user)
    @Transactional
    public TripResponse getTripById(UUID tripId) {
        UUID userId = authUtils.getCurrentUserId();

        // findByIdAndUserId ensures users can ONLY see their own trips
        Trip trip = tripRepository
                .findByIdAndUserId(tripId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Trip", tripId)
                );

        return mapToResponse(trip);
    }

    // DELETE a trip
    @Transactional
    public void deleteTrip(UUID tripId) {
        UUID userId = authUtils.getCurrentUserId();

        Trip trip = tripRepository
                .findByIdAndUserId(tripId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Trip", tripId)
                );

        // Only DRAFT or CANCELLED trips can be deleted
        if (trip.getStatus() == Trip.TripStatus.ACTIVE) {
            throw new BusinessException(
                    "Cannot delete an active trip. Cancel it first."
            );
        }

        tripRepository.delete(trip);
        log.info("Trip deleted: {}", tripId);
    }

    // --- Private Mapper ---
    private TripResponse mapToResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .startLocation(trip.getStartLocation())
                .budgetUsd(trip.getBudgetUsd())
                .numberOfDays(trip.getNumberOfDays())
                .travelStyle(trip.getTravelStyle())
                .groupType(trip.getGroupType())
                .adventureLevel(trip.getAdventureLevel())
                .status(trip.getStatus())
                .foodPreferences(trip.getFoodPreferences())
                .healthConditions(trip.getHealthConditions())
                .aiSummary(trip.getAiSummary())
                .createdAt(trip.getCreatedAt())
                .userId(trip.getUser().getId())
                .userName(trip.getUser().getName())
                .build();
    }
}