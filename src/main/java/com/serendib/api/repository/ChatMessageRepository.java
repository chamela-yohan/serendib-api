package com.serendib.api.repository;

import com.serendib.api.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, UUID> {

    // Get last N messages for context window
    // We don't want to send 100 messages to AI every time
    List<ChatMessage> findTop10BySessionIdOrderByCreatedAtAsc(UUID sessionId);
}