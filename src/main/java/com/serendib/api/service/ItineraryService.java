package com.serendib.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serendib.api.ai.ItineraryPromptBuilder;
import com.serendib.api.common.AuthUtils;
import com.serendib.api.dto.response.ItineraryResponse;
import com.serendib.api.entity.Itinerary;
import com.serendib.api.entity.Trip;
import com.serendib.api.event.ItineraryGeneratedEvent;
import com.serendib.api.event.SerendibEventProducer;
import com.serendib.api.event.TripStatusChangedEvent;
import com.serendib.api.exception.BusinessException;
import com.serendib.api.exception.ResourceNotFoundException;
import com.serendib.api.repository.ItineraryRepository;
import com.serendib.api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItineraryService {

    private final ChatClient chatClient;              // Spring AI
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    private final ItineraryPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;           // JSON parser
    private final AuthUtils authUtils;

    private final SerendibEventProducer eventProducer;

    @Transactional
    public ItineraryResponse generateItinerary(UUID tripId) {

        UUID userId = authUtils.getCurrentUserId();

        // Load the trip — verify ownership
        Trip trip = tripRepository
                .findByIdAndUserId(tripId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Trip", tripId)
                );

        // Don't regenerate if already planned
        if (itineraryRepository.existsByTripId(tripId)) {
            throw new BusinessException(
                    "Itinerary already exists for this trip. " +
                            "Delete it first to regenerate."
            );
        }

        log.info("Generating itinerary for trip: {} ({})",
                trip.getTitle(), tripId);

        // Build prompts
        String systemPrompt = promptBuilder.buildSystemPrompt();
        String userPrompt   = promptBuilder.buildUserPrompt(trip);

        // Call GEMINI via Spring AI
        String rawJson = chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        log.info("Received AI response for trip: {}", tripId);

        // Parse JSON response into typed object
        Itinerary.DayPlan[] days;
        ItineraryAiOutput aiOutput;

        String cleanJson = rawJson
                .trim()
                .replaceAll("^```json\\s*", "")   // remove ```json at start
                .replaceAll("^```\\s*", "")       // remove ``` at start
                .replaceAll("```\\s*$", "");

        try {
            aiOutput = objectMapper.readValue(cleanJson, ItineraryAiOutput.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", rawJson, e);
            throw new BusinessException(
                    "AI returned an unexpected format. Please try again."
            );
        }

        // Build and save Itinerary entity
        Itinerary itinerary = Itinerary.builder()
                .trip(trip)
                .days(aiOutput.getDays())
                .estimatedTotalCost(aiOutput.getEstimatedTotalCost())
                .packingSuggestions(aiOutput.getPackingSuggestions())
                .generalTips(aiOutput.getGeneralTips())
                .generatedBy("gemini-3.5-flash")
                .build();

        Itinerary saved = itineraryRepository.save(itinerary);

        // Update trip status: DRAFT → PLANNED
        trip.setStatus(Trip.TripStatus.PLANNED);
        tripRepository.save(trip);

        log.info("Itinerary saved for trip: {}", tripId);

        eventProducer.publishTripStatusChanged(
                TripStatusChangedEvent.builder()
                        .tripId(trip.getId())
                        .tripTitle(trip.getTitle())
                        .userEmail(trip.getUser().getEmail())
                        .previousStatus("DRAFT")
                        .newStatus("PLANNED")
                        .changedAt(java.time.LocalDateTime.now())
                        .build()
        );

        eventProducer.publishItineraryGenerated(
                ItineraryGeneratedEvent.builder()
                        .itineraryId(saved.getId())
                        .tripId(trip.getId())
                        .tripTitle(trip.getTitle())
                        .userEmail(trip.getUser().getEmail())
                        .numberOfDays(trip.getNumberOfDays())
                        .generatedAt(java.time.LocalDateTime.now())
                        .build()
        );

        return mapToResponse(saved, trip);
    }

    @Transactional(readOnly = true)
    public ItineraryResponse getItinerary(UUID tripId) {

        UUID userId = authUtils.getCurrentUserId();

        // Verify trip ownership first
        Trip trip = tripRepository
                .findByIdAndUserId(tripId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Trip", tripId)
                );

        Itinerary itinerary = itineraryRepository
                .findByTripId(tripId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No itinerary found for trip. " +
                                        "Generate one first."
                        )
                );

        return mapToResponse(itinerary, trip);
    }

    // --------- Private helpers -------

    private ItineraryResponse mapToResponse(Itinerary itinerary, Trip trip) {
        return ItineraryResponse.builder()
                .id(itinerary.getId())
                .tripId(trip.getId())
                .tripTitle(trip.getTitle())
                .days(itinerary.getDays())
                .estimatedTotalCost(itinerary.getEstimatedTotalCost())
                .packingSuggestions(itinerary.getPackingSuggestions())
                .generalTips(itinerary.getGeneralTips())
                .generatedBy(itinerary.getGeneratedBy())
                .createdAt(itinerary.getCreatedAt())
                .build();
    }

    // Inner class matching the AI JSON output shape
    // Jackson deserializes the AI response into this
    @lombok.Data
    public static class ItineraryAiOutput {
        private java.util.List<Itinerary.DayPlan> days;
        private java.math.BigDecimal estimatedTotalCost;
        private java.util.List<String> packingSuggestions;
        private java.util.List<String> generalTips;
    }
}