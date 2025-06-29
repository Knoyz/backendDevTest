package com.backendDevTest.myApp.infrastructure.adapters.in.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.backendDevTest.myApp.application.ports.ProductAPIPort;
import com.backendDevTest.myApp.domain.events.ProductsSimilarIdsEvent;
import com.backendDevTest.myApp.domain.events.ResponseProductDetailsEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaAdapterConsumer {

    private final ProductAPIPort productAPIPort;

    @KafkaListener(topics = "similar-product-ids", groupId = "product-service")
    public void listenForSimilarProductIds(ProductsSimilarIdsEvent event) {
        log.debug("Received event for similar product IDs: {}", event);
        var productsIds = event.productIds();
        if (productsIds != null) {
            log.debug("Processing similar product IDs: {}", productsIds);

            event.productIds().subscribe(
                    productId -> {
                        log.debug("Fetching similar product IDs for product ID: {}", productId);
                        productAPIPort.requestProductDetails(productId);
                    },
                    error -> log.error("Error fetching similar product IDs: {}", error.getMessage()),
                    () -> log.info("Completed processing similar product IDs for event: {}", event));
        } else {
            log.warn("No similar product IDs found for product ID: {}", event.productIds());
        }
    }

    @KafkaListener(topics = "product-details", groupId = "product-service")
    public void listenForProductDetails(ResponseProductDetailsEvent event) {
        log.debug("Received event for product details: {}", event);
        var productDetails = event;

    }
}
