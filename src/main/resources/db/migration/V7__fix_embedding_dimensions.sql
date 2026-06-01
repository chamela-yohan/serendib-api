-- V7__fix_embedding_dimensions.sql
-- gemini-embedding-2 produces 3072 dimensions
-- Skipping vector index (pgvector version limit)
-- Sequential scan is fine for our small knowledge base (12 entries)

DROP INDEX IF EXISTS idx_knowledge_base_embedding;

ALTER TABLE knowledge_base
ALTER COLUMN embedding TYPE vector(3072)
    USING embedding::vector(3072);