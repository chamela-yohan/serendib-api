package com.serendib.api.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;  // ← changed
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(GoogleGenAiChatModel chatModel) {  // ← changed
        return ChatClient.builder(chatModel).build();
    }
}