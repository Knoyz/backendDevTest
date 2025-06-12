package com.myapps.myapp.infrastructure.adapters.out.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import com.myapps.myapp.domain.port.out.EventPublisherPort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private static final String SIMILAR_PRODUCTS_IDS = "SIMILAR_PRODUCTS_IDS";
    private static final String SIMILAR_PRODUCTS_DETAILS_FETCH = "SIMILAR_PRODUCTS_DETAILS_FETCH";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishFetchProductDetailsEvent(String eventType, String productId) {
        kafkaTemplate.send(SIMILAR_PRODUCTS_DETAILS_FETCH, eventType, productId)
                .exceptionally(failure -> {
                    log.error("Failed to publish event: {}", failure.getMessage());
                    throw new MessagingException("Failed to publish event", failure);
                });
    }

    @Override
    public void publishGetSimilarProductsEvent(String eventType, String productId) {
        log.info("Publishing event: {} for product ID: {}", eventType, productId);
        kafkaTemplate.send(SIMILAR_PRODUCTS_IDS, eventType, productId)
                .exceptionally(failure -> {
                    log.error("Failed to publish event: {}", failure.getMessage());
                    throw new MessagingException("Failed to publish event", failure);
                });
    }
}
