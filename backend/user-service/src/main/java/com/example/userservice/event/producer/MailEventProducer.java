package com.example.userservice.event.producer;

import com.example.userservice.dto.MailEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MailEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MailEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMailEvent(MailEvent event) {
        kafkaTemplate.send("send-mail-events", event);
    }
}
