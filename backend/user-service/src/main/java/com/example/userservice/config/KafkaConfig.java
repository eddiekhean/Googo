package com.example.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaConfig {
    @Bean
    public NewTopic sendMailEvents() {
        return TopicBuilder.name("send-mail-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
