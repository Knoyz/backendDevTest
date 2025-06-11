package com.myapps.myapp.infrastructure.adapters.out;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.myapps.myapp.domain.port.out.EventPublisherPort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishEvent(String eventType, String productId) {
        kafkaTemplate.send("product-events", eventType, productId);
    }
}
