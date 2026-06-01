package com.serendib.api.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Calls Google's Embedding REST API directly
@Component
@Slf4j
public class GoogleEmbeddingClient {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    public GoogleEmbeddingClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // text-embedding-004 produces 768-dimension vectors
    private static final String EMBEDDING_URL =
            "https://generativelanguage.googleapis.com/v1/models/" +
                    "gemini-embedding-2:embedContent?key=";

    public float[] embed(String text) {
        try {
            // Build request body
            String requestBody = """
                    {
                      "content": {
                        "parts": [{ "text": "%s" }]
                      }
                    }
                    """.formatted(
                    text.replace("\"", "\\\"")
                            .replace("\n", " ")
            );

            // Make HTTP call using Java's built-in HttpClient
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EMBEDDING_URL + apiKey.trim()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Embedding API error: {}", response.body());
                throw new RuntimeException("Embedding failed: " + response.statusCode());
            }

            // Parse response: { "embedding": { "values": [0.1, 0.2, ...] } }
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode values = root.path("embedding").path("values");

            float[] embedding = new float[values.size()];
            for (int i = 0; i < values.size(); i++) {
                embedding[i] = (float) values.get(i).asDouble();
            }

            return embedding;

        } catch (Exception e) {
            log.error("Failed to generate embedding", e);
            throw new RuntimeException("Embedding generation failed", e);
        }
    }
}