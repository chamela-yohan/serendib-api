package com.serendib.api.repository;

import com.serendib.api.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, UUID> {

    /*
        - Vector similarity search using pgvector
        - Finds the top K most similar knowledge chunks to a query vector
        - <=> : cosine distance operator in pgvector
        - ORDER BY distance ASC: most similar first
     */

    @Query(value = """
    SELECT id, content, category, source, created_at, null AS embedding
    FROM knowledge_base
    ORDER BY embedding <=> CAST(:embedding AS vector)
    LIMIT :limit
    """, nativeQuery = true)
    List<KnowledgeBase> findSimilar(
            @Param("embedding") String embedding,
            @Param("limit") int limit
    );


    List<KnowledgeBase> findByCategory(String category);
}
