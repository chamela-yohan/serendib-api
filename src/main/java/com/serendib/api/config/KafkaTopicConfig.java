package com.serendib.api.config;

import com.serendib.api.event.SerendibEventProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic sosTopic() {
        return TopicBuilder.name(SerendibEventProducer.TOPIC_SOS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic itineraryTopic() {
        return TopicBuilder.name(SerendibEventProducer.TOPIC_ITINERARY)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tripStatusTopic() {
        return TopicBuilder.name(SerendibEventProducer.TOPIC_TRIP_STATUS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}