-- V5__create_itineraries_table.sql

CREATE TABLE itineraries
(
    id                   UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    trip_id              UUID      NOT NULL REFERENCES trips (id) ON DELETE CASCADE,

    -- Full structured itinerary stored as JSONB
    -- We store the whole thing as JSON for flexibility
    -- individual days, places, hotels all queryable
    days                 JSONB     NOT NULL,

    estimated_total_cost DECIMAL(10, 2),
    packing_suggestions  TEXT[],
    general_tips         TEXT[],

    -- Which AI model generated this
    generated_by         VARCHAR(50)        DEFAULT 'gemini-3.5-flash',

    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW()
);

-- One trip has one itinerary
CREATE UNIQUE INDEX idx_itineraries_trip_id ON itineraries (trip_id);