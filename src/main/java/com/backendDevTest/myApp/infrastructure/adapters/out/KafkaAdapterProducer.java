package com.backendDevTest.myApp.infrastructure.adapters.out;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.backendDevTest.myApp.application.ports.ProductAPIPort;
import com.backendDevTest.myApp.domain.events.RequestDataWithProductIdEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaAdapterProducer implements ProductAPIPort {

    private final KafkaTemplate<String, RequestDataWithProductIdEvent> requestDataWithProductIdKafkaTemplate;

    /**
     * Requests product details for a given product ID by sending a message to the
     * Kafka topic "product-details".
     * 
     * @param productId A unique identifier for the product for which details are
     *                  requested.
     */
    @Override
    public Mono<Void> requestProductDetails(String productId) {

        return Mono.fromCallable(
                () -> requestDataWithProductIdKafkaTemplate.send("product-details", productId,
                        new RequestDataWithProductIdEvent(Mono.just(productId))))
                .doOnSuccess(result -> {
                    if (result != null) {
                        log.debug("Request sent for product details with ID: {}", productId);
                    } else {
                        log.debug("Failed to send request for product details with ID: {}", productId);
                    }
                }).then();
    }

    /**
     * Requests similar product IDs for a given product ID by sending a message to
     * the Kafka topic "similar-product-ids".
     * 
     * @param productId A unique identifier for the product for which similar
     *                  product IDs are requested.
     */
    @Override
    public Mono<Void> requestSimilarProductIds(String productId) {

        return Mono.fromCallable(
                () -> requestDataWithProductIdKafkaTemplate.send("similar-product-ids", productId,
                        new RequestDataWithProductIdEvent(Mono.just(productId))))
                .doOnSuccess(result -> {
                    if (result != null) {
                        log.debug("Request sent for similar product IDs with ID: {}", productId);
                    } else {
                        log.debug("Failed to send request for similar product IDs with ID: {}", productId);
                    }
                }).then();

    }
}
