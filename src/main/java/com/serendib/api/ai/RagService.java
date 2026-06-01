package com.serendib.api.ai;

import com.serendib.api.entity.KnowledgeBase;
import com.serendib.api.repository.KnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final GoogleEmbeddingClient embeddingClient;
    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public String findRelevantContext(String question, int topK) {

        // Convert question to vector using our direct client
        float[] questionEmbedding = embeddingClient.embed(question);

        // Convert to PostgreSQL vector string format
        String vectorStr = Arrays.toString(questionEmbedding)
                .replace("{", "[")
                .replace("}", "]");

        // Search for similar knowledge chunks
        List<KnowledgeBase> relevant =
                knowledgeBaseRepository.findSimilar(vectorStr, topK);

        if (relevant.isEmpty()) {
            return "No specific context available.";
        }

        // Combine into context string
        String context = relevant.stream()
                .map(KnowledgeBase::getContent)
                .collect(Collectors.joining("\n\n---\n\n"));

        log.debug("Found {} relevant chunks for: {}", relevant.size(), question);
        return context;
    }
}