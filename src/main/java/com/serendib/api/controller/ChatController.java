package com.serendib.api.controller;

import com.serendib.api.common.ApiResponse;
import com.serendib.api.dto.request.ChatRequest;
import com.serendib.api.dto.response.ChatResponse;
import com.serendib.api.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // POST /api/v1/chat/sessions
    // Start a new chat session (optionally linked to a trip)
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<ChatResponse.SessionInfo>> startSession(
            @RequestParam(required = false) UUID tripId) {

        ChatResponse.SessionInfo session = chatService.startSession(tripId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Chat session started", session
                ));
    }

    // POST /api/v1/chat/sessions/{sessionId}/messages
    // Send a message and get AI response
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @PathVariable UUID sessionId,
            @RequestBody @Valid ChatRequest request) {

        ChatResponse response = chatService.chat(sessionId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Message sent", response)
        );
    }

    // GET /api/v1/chat/sessions/{sessionId}/messages
    // Get chat history
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getHistory(
            @PathVariable UUID sessionId) {

        List<ChatResponse> history = chatService.getHistory(sessionId);
        return ResponseEntity.ok(
                ApiResponse.success("Chat history retrieved", history)
        );
    }
}