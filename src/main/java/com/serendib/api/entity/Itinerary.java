package com.serendib.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "itineraries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    // One trip has one itinerary
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    // Store the entire day-by-day plan as JSONB
    // List of DayPlan objects serialized to JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<DayPlan> days;

    @Column(name = "estimated_total_cost", precision = 10, scale = 2)
    private BigDecimal estimatedTotalCost;

    @Column(name = "packing_suggestions", columnDefinition = "TEXT[]")
    private List<String> packingSuggestions;

    @Column(name = "general_tips", columnDefinition = "TEXT[]")
    private List<String> generalTips;

    @Column(name = "generated_by")
    private String generatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // -------- Nested classes stored as JSONB
    // These are NOT entities — they're just data classes
    // stored as JSON inside the itineraries table

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayPlan {
        private Integer dayNumber;
        private String theme;           // "Cultural Exploration in Colombo"
        private List<PlaceToVisit> places;
        private HotelSuggestion hotel;
        private List<String> meals;     // ["Cafe Kumbuk", "Ministry of Crab"]
        private String transport;       // "Tuk-tuk to Galle Face, then bus"
        private BigDecimal estimatedDayCost;
        private List<String> tips;      // day-specific tips
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceToVisit {
        private String name;
        private String description;
        private String location;
        private Integer visitDurationMinutes;
        private String bestTimeToVisit;
        private BigDecimal entranceFee;
        private String safetyNote;      // important for health conditions
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotelSuggestion {
        private String name;
        private String area;
        private String type;            // "Budget guesthouse" / "3-star hotel"
        private BigDecimal pricePerNight;
        private List<String> highlights;
    }
}