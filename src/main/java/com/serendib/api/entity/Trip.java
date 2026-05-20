package com.serendib.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "trips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    // Many trips belong to ONE user
    // @ManyToOne = "many trips → one user"
    // FetchType.LAZY = don't load User from DB unless we explicitly ask
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "start_location", nullable = false)
    private String startLocation;

    @Column(name = "budget_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal budgetUsd;

    @Column(name = "number_of_days", nullable = false)
    private Integer numberOfDays;

    // @Enumerated(EnumType.STRING) = store "BUDGET" not 0 in DB
    @Enumerated(EnumType.STRING)
    @Column(name = "travel_style", nullable = false)
    private TravelStyle travelStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false)
    private GroupType groupType;

    @Enumerated(EnumType.STRING)
    @Column(name = "adventure_level", nullable = false)
    private AdventureLevel adventureLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TripStatus status = TripStatus.DRAFT;

    // PostgreSQL TEXT[] array
    @Column(name = "food_preferences", columnDefinition = "TEXT[]")
    private List<String> foodPreferences;

    // PostgreSQL JSONB — stores Map as JSON
    // { "heartCondition": true, "asthma": false }
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "health_conditions", columnDefinition = "jsonb")
    private Map<String, Object> healthConditions;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

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

    // --- Enums ---
    // Defined inside Trip class — they belong to Trip concept

    public enum TravelStyle {
        BUDGET, COMFORT, LUXURY
    }

    public enum GroupType {
        SOLO, COUPLE, FAMILY, FRIENDS
    }

    public enum AdventureLevel {
        LOW, MEDIUM, HIGH
    }

    public enum TripStatus {
        DRAFT, PLANNED, ACTIVE, COMPLETED, CANCELLED
    }
}