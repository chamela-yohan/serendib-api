package com.serendib.api.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SerendibEventProducer {
    // Topic name constants - single source of truth
    public static final String TOPIC_SOS = "serendib.sos.triggered";
    public static final String TOPIC_ITINERARY = "serendib.itinerary.generated";
    public static final String TOPIC_TRIP_STATUS = "serendib.trip.status.changed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSosTriggered(SosTriggeredEvent event) {
        log.info("Publishing SOS event for user: {}", event.getUserEmail());
        kafkaTemplate.send(TOPIC_SOS, event.getAlertId().toString(), event);
    }

    public void publishItineraryGenerated(ItineraryGeneratedEvent event) {
        log.info("Publishing itinerary event for trip: {}", event.getTripTitle());
        kafkaTemplate.send(TOPIC_ITINERARY, event.getTripId().toString(), event);
    }

    public void publishTripStatusChanged(TripStatusChangedEvent event) {
        log.info("Publishing trip status change: {} → {} for trip: {}",
                event.getPreviousStatus(), event.getNewStatus(), event.getTripTitle());
        kafkaTemplate.send(TOPIC_TRIP_STATUS, event.getTripId().toString(), event);
    }
}
