package com.serendib.api.service;

import com.serendib.api.ai.RagService;
import com.serendib.api.common.AuthUtils;
import com.serendib.api.dto.request.ChatRequest;
import com.serendib.api.dto.response.ChatResponse;
import com.serendib.api.entity.ChatMessage;
import com.serendib.api.entity.ChatSession;
import com.serendib.api.entity.Trip;
import com.serendib.api.entity.User;
import com.serendib.api.exception.ResourceNotFoundException;
import com.serendib.api.repository.ChatMessageRepository;
import com.serendib.api.repository.ChatSessionRepository;
import com.serendib.api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatClient chatClient;
    private final RagService ragService;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final TripRepository tripRepository;
    private final AuthUtils authUtils;

    // Start a new chat session
    @Transactional
    public ChatResponse.SessionInfo startSession(UUID tripId) {

        User currentUser = authUtils.getCurrentUser();

        ChatSession.ChatSessionBuilder builder = ChatSession.builder()
                .user(currentUser)
                .title("Sri Lanka Travel Chat");

        // Optionally link to a trip
        if (tripId != null) {
            Trip trip = tripRepository
                    .findByIdAndUserId(tripId, currentUser.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Trip", tripId)
                    );
            builder.trip(trip);
            builder.title("Chat about: " + trip.getTitle());
        }

        ChatSession session = sessionRepository.save(builder.build());

        return ChatResponse.SessionInfo.builder()
                .sessionId(session.getId())
                .title(session.getTitle())
                .createdAt(session.getCreatedAt())
                .build();
    }

    // Send a message and get AI response
    @Transactional
    public ChatResponse chat(UUID sessionId, ChatRequest request) {

        UUID userId = authUtils.getCurrentUserId();

        // 1. Verify session belongs to user
        ChatSession session = sessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Chat session", sessionId)
                );

        // 2. Get last 10 messages for conversation context
        List<ChatMessage> history =
                messageRepository.findTop10BySessionIdOrderByCreatedAtAsc(sessionId);

        // 3. RAG — find relevant Sri Lanka knowledge
        String relevantContext =
                ragService.findRelevantContext(request.getMessage(), 3);

        // 4. Build conversation history string for the prompt
        String historyText = history.stream()
                .map(m -> m.getRole().toUpperCase() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        // 5. Build system prompt with RAG context
        String systemPrompt = buildSystemPrompt(relevantContext);

        // 6. Build user message with history
        String userMessage = historyText.isEmpty()
                ? request.getMessage()
                : historyText + "\nUSER: " + request.getMessage();

        // 7. Call Gemini
        log.info("Sending chat message for session: {}", sessionId);
        String aiResponse = chatClient
                .prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();

        // 8. Save user message to DB
        messageRepository.save(ChatMessage.builder()
                .session(session)
                .role("user")
                .content(request.getMessage())
                .build());

        // 9. Save AI response to DB
        messageRepository.save(ChatMessage.builder()
                .session(session)
                .role("assistant")
                .content(aiResponse)
                .build());

        return ChatResponse.builder()
                .sessionId(sessionId)
                .message(aiResponse)
                .role("assistant")
                .build();
    }

    // Get chat history for a session
    @Transactional(readOnly = true)
    public List<ChatResponse> getHistory(UUID sessionId) {

        UUID userId = authUtils.getCurrentUserId();

        sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Chat session", sessionId)
                );

        return messageRepository
                .findTop10BySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(m -> ChatResponse.builder()
                        .sessionId(sessionId)
                        .message(m.getContent())
                        .role(m.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    // Build system prompt with RAG context injected
    private String buildSystemPrompt(String context) {
        return """
            You are SerendibAI, a friendly and knowledgeable Sri Lanka
            travel assistant. You help travelers plan safe, enjoyable,
            and budget-friendly trips to Sri Lanka.

            Use the following verified Sri Lanka travel information
            to answer questions accurately:

            === VERIFIED CONTEXT ===
            %s
            === END CONTEXT ===

            Guidelines:
            - Be friendly and conversational
            - Give specific, actionable advice
            - Always prioritize traveler safety
            - Mention health considerations when relevant
            - Keep responses concise (2-4 sentences unless detail needed)
            - If asked something outside Sri Lanka travel, politely redirect
            """.formatted(context);
    }
}