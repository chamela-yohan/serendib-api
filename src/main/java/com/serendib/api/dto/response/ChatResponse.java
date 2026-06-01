package com.serendib.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private UUID sessionId;
    private String message;
    private String role;

    // Nested DTO for session info
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionInfo {
        private UUID sessionId;
        private String title;
        private LocalDateTime createdAt;
    }
}