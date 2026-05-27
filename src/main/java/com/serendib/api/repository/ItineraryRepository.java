package com.serendib.api.repository;

import com.serendib.api.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, UUID> {

    // Find itinerary by trip ID
    Optional<Itinerary> findByTripId(UUID tripId);

    // Check if itinerary exists for a trip
    boolean existsByTripId(UUID tripId);
}