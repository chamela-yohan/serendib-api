package com.serendib.api.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SerendibEventConsumer {

    // This is where we'd plug in: email service, push notifications,
    // admin dashboard updates, analytics tracking, etc.
    // Just for now we richly — demonstrating the pipeline works end-to-end.

    @KafkaListener(
            topics = SerendibEventProducer.TOPIC_SOS,
            groupId = "serendib-group"
    )
    public void handleSosTriggered(SosTriggeredEvent event) {
        log.warn("""
            ----------------------------
            🚨 SOS ALERT RECEIVED
            ----------------------------
            Alert ID  : {}
            User      : {}
            Location  : [{}, {}]
            Message   : {}
            Hospital  : {}
            Police    : {}
            Time      : {}
            ----------------------------
            """,
                event.getAlertId(),
                event.getUserEmail(),
                event.getLatitude(), event.getLongitude(),
                event.getMessage(),
                event.getNearestHospital(),
                event.getNearestPolice(),
                event.getTriggeredAt()
        );
    }

    @KafkaListener(
            topics = SerendibEventProducer.TOPIC_ITINERARY,
            groupId = "serendib-group"
    )
    public void handleItineraryGenerated(ItineraryGeneratedEvent event) {
        log.info("""
            ----------------------------
            ✅ ITINERARY GENERATED
            ----------------------------
            Itinerary : {}
            Trip      : {} ({} days)
            User      : {}
            Time      : {}
            ----------------------------
            """,
                event.getItineraryId(),
                event.getTripTitle(), event.getNumberOfDays(),
                event.getUserEmail(),
                event.getGeneratedAt()
        );
    }

    @KafkaListener(
            topics = SerendibEventProducer.TOPIC_TRIP_STATUS,
            groupId = "serendib-group"
    )
    public void handleTripStatusChanged(TripStatusChangedEvent event) {
        log.info("""
            ----------------------------
            🔄 TRIP STATUS CHANGED
            ----------------------------
            Trip      : {}
            Status    : {} → {}
            User      : {}
            Time      : {}
            ----------------------------
            """,
                event.getTripTitle(),
                event.getPreviousStatus(), event.getNewStatus(),
                event.getUserEmail(),
                event.getChangedAt()
        );
    }
}