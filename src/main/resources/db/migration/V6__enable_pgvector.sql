-- V6__enable_pgvector.sql
-- Enable the pgvector extension for semantic search

CREATE EXTENSION IF NOT EXISTS vector;

-- Knowledge base table
-- Stores Sri Lanka travel facts as text + vector embeddings
CREATE TABLE knowledge_base (
                                id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                content     TEXT NOT NULL,          -- the actual text chunk
                                category    VARCHAR(50) NOT NULL,   -- 'safety', 'weather', 'food', etc.
                                source      VARCHAR(100),           -- where this info came from
                                embedding   vector(768),            -- Google's embedding size is 768
                                created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- This special index makes vector similarity search FAST
-- Like a regular index but for vectors
CREATE INDEX idx_knowledge_base_embedding
    ON knowledge_base
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);

-- Chat sessions table
CREATE TABLE chat_sessions (
                               id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               trip_id     UUID REFERENCES trips(id) ON DELETE SET NULL,
                               title       VARCHAR(200),
                               created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Chat messages table
CREATE TABLE chat_messages (
                               id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               session_id  UUID NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
                               role        VARCHAR(20) NOT NULL    -- 'user' or 'assistant'
                                   CHECK (role IN ('user', 'assistant')),
                               content     TEXT NOT NULL,
                               created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_messages_session ON chat_messages(session_id);
CREATE INDEX idx_chat_sessions_user    ON chat_sessions(user_id);