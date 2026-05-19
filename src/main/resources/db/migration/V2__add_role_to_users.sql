-- V2__add_role_to_users.sql
-- Add role column to existing users table
ALTER TABLE users
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'TRAVELER';