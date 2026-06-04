-- V8__create_emergency_tables.sql

CREATE TABLE emergency_services (
                                    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    name        VARCHAR(255)    NOT NULL,
                                    type        VARCHAR(50)     NOT NULL CHECK (type IN ('HOSPITAL','POLICE','PHARMACY','FIRE')),
                                    address     VARCHAR(500),
                                    province    VARCHAR(100),
                                    district    VARCHAR(100),
                                    latitude    DOUBLE PRECISION NOT NULL,
                                    longitude   DOUBLE PRECISION NOT NULL,
                                    phone       VARCHAR(50),
                                    phone2      VARCHAR(50),
                                    is_24h      BOOLEAN DEFAULT false,
                                    notes       TEXT,
                                    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE sos_alerts (
                            id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id         UUID REFERENCES users(id),
                            latitude        DOUBLE PRECISION,
                            longitude       DOUBLE PRECISION,
                            message         TEXT,
                            status          VARCHAR(50) DEFAULT 'ACTIVE',
                            created_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_emergency_type     ON emergency_services(type);
CREATE INDEX idx_emergency_province ON emergency_services(province);