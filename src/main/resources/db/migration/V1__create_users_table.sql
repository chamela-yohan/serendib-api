-- V1__create_users_table.sql
-- This creates first table: users
-- Flyway runs this automatically when the app starts

CREATE TABLE users (
                       id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name        VARCHAR(100) NOT NULL,
                       email       VARCHAR(150) NOT NULL UNIQUE,
                       password    VARCHAR(255) NOT NULL,
                       created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index on email for fast lookups during login
CREATE INDEX idx_users_email ON users(email);