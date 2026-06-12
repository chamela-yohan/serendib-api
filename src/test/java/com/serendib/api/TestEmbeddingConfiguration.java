package com.serendib.api;

import com.serendib.api.ai.GoogleEmbeddingClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestEmbeddingConfiguration {

    // Provides a pre-configured mock BEFORE ApplicationRunners fire
    // Returns a valid zero-vector so the seeder can run without NPE
    @Bean
    @Primary
    public GoogleEmbeddingClient googleEmbeddingClient() {
        GoogleEmbeddingClient mock = Mockito.mock(GoogleEmbeddingClient.class);
        Mockito.when(mock.embed(Mockito.anyString()))
                .thenReturn(new float[3072]);
        return mock;
    }
}