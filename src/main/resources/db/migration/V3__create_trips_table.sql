-- V3__create_trips_table.sql

-- Enum type for travel style
CREATE TYPE travel_style AS ENUM (
    'BUDGET',
    'COMFORT',
    'LUXURY'
);

-- Enum type for group type
CREATE TYPE group_type AS ENUM (
    'SOLO',
    'COUPLE',
    'FAMILY',
    'FRIENDS'
);

-- Enum type for adventure level
CREATE TYPE adventure_level AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH'
);

-- Enum type for trip status
CREATE TYPE trip_status AS ENUM (
    'DRAFT',        -- just created, not planned yet
    'PLANNED',      -- AI has generated the itinerary
    'ACTIVE',       -- trip is currently happening
    'COMPLETED',    -- trip is done
    'CANCELLED'     -- trip was cancelled
);

-- Main trips table
CREATE TABLE trips (
                       id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       title           VARCHAR(200) NOT NULL,
                       start_location  VARCHAR(100) NOT NULL DEFAULT 'Colombo',
                       budget_usd      DECIMAL(10,2) NOT NULL,
                       number_of_days  INTEGER NOT NULL,
                       travel_style    travel_style NOT NULL,
                       group_type      group_type NOT NULL,
                       adventure_level adventure_level NOT NULL,
                       status          trip_status NOT NULL DEFAULT 'DRAFT',

    -- Stored as JSON arrays in PostgreSQL
                       food_preferences    TEXT[],     -- ["vegetarian", "seafood"]
                       health_conditions   JSONB,      -- { "heartCondition": true, "asthma": false }

    -- AI generated summary (filled after AI planning)
                       ai_summary      TEXT,

                       created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index so we can quickly find all trips by a user
CREATE INDEX idx_trips_user_id ON trips(user_id);
CREATE INDEX idx_trips_status  ON trips(status);