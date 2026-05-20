-- V4__fix_trips_enum_columns.sql
-- Fix: Replace custom PostgreSQL enum types with VARCHAR + CHECK constraints
-- Hibernate handles Java enum -> String conversion natively
-- We don't need PostgreSQL custom enum types

-- Drop the trips table first (we're in dev, no real data yet)
DROP TABLE IF EXISTS trips;

-- Drop the custom PostgreSQL enum types
DROP TYPE IF EXISTS travel_style;
DROP TYPE IF EXISTS group_type;
DROP TYPE IF EXISTS adventure_level;
DROP TYPE IF EXISTS trip_status;

-- Recreate trips table using VARCHAR for enum columns
CREATE TABLE trips (
                       id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       title           VARCHAR(200) NOT NULL,
                       start_location  VARCHAR(100) NOT NULL DEFAULT 'Colombo',
                       budget_usd      DECIMAL(10,2) NOT NULL,
                       number_of_days  INTEGER NOT NULL,

    -- VARCHAR + CHECK constraint instead of custom PG enum
    -- Hibernate sends "BUDGET", "COMFORT", "LUXURY" as strings
                       travel_style    VARCHAR(20) NOT NULL
                           CHECK (travel_style IN ('BUDGET','COMFORT','LUXURY')),

                       group_type      VARCHAR(20) NOT NULL
                           CHECK (group_type IN ('SOLO','COUPLE','FAMILY','FRIENDS')),

                       adventure_level VARCHAR(20) NOT NULL
                           CHECK (adventure_level IN ('LOW','MEDIUM','HIGH')),

                       status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
                           CHECK (status IN ('DRAFT','PLANNED','ACTIVE','COMPLETED','CANCELLED')),

                       food_preferences    TEXT[],
                       health_conditions   JSONB,
                       ai_summary          TEXT,

                       created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trips_user_id ON trips(user_id);
CREATE INDEX idx_trips_status  ON trips(status);