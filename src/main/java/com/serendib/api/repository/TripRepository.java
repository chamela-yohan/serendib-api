package com.serendib.api.repository;

import com.serendib.api.entity.Trip;
import com.serendib.api.entity.Trip.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    // Find all trips for a specific user
    // SQL: SELECT * FROM trips WHERE user_id = ? ORDER BY created_at DESC
    List<Trip> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Find a specific trip that belongs to a specific user
    // (prevents users from accessing other users' trips)
    // SQL: SELECT * FROM trips WHERE id = ? AND user_id = ?
    Optional<Trip> findByIdAndUserId(UUID id, UUID userId);

    // Count trips by status for a user
    long countByUserIdAndStatus(UUID userId, TripStatus status);
}