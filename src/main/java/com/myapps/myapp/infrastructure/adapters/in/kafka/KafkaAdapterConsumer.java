package com.myapps.myapp.infrastructure.adapters.in.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.myapps.myapp.domain.events.ProductsSimilarIdsEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaAdapterConsumer {

    @KafkaListener(topics = "similar-product-ids", groupId = "product-service")
    public void listenForSimilarProductIds(ProductsSimilarIdsEvent event) {
        log.debug("Received event for similar product IDs: {}", event);
        var productsIds = event.productIds();
        if (productsIds != null) {
            log.debug("Processing similar product IDs: {}", productsIds);

            event.productIds().subscribe(
                    productId -> log.debug("Fetching similar product IDs for product ID: {}", productId),
                    error -> log.error("Error fetching similar product IDs: {}", error.getMessage()),
                    () -> log.info("Completed processing similar product IDs for event: {}", event));
        } else {
            log.warn("No similar product IDs found for product ID: {}", event.productIds());
        }
    }

    @KafkaListener(topics = "similar-product-details-fetch", groupId = "product-service")
    public void listenForSimilarProductDetails(ProductsSimilarIdsEvent event) {

        log.debug("Received event for similar product details: {}", event);
        var productsIds = event.productIds();
        if (productsIds != null) {
            log.debug("Processing similar product details for IDs: {}", productsIds);

            event.productIds().subscribe(
                    productId -> log.debug("Fetching product details for similar product ID: {}", productId),
                    error -> log.error("Error fetching similar product details: {}", error.getMessage()),
                    () -> log.info("Completed processing similar product details for event: {}", event));
        } else {
            log.warn("No similar product details found for product ID: {}", event.productIds());
        }
    }

}
