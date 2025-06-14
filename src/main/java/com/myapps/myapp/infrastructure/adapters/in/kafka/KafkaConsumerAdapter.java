package com.myapps.myapp.infrastructure.adapters.in.kafka;

import java.time.Duration;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.myapps.myapp.domain.events.ProductDetailsChangedEvent;
import com.myapps.myapp.domain.port.in.EventConsumerPort;
import com.myapps.myapp.domain.port.out.CachePort;
import com.myapps.myapp.infrastructure.mapper.ProductDetailsEventMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaConsumerAdapter implements EventConsumerPort {

    private final CachePort cachePort;

    @Override
    @KafkaListener(topics = "PRODUCT_DETAILS_CHANGED", groupId = "product-service", containerFactory = "productDetailsContainerFactory")
    public void consumeProductDetailsChangedEvent(ProductDetailsChangedEvent event) {

        log.debug("Received event for similar product details: {}", event);

        var productChanged = ProductDetailsEventMapper.toDomain(event);
        if (productChanged != null) {
            log.debug("Changing if cache product details for product : {}", productChanged);

            cachePort.cacheProductDetails(productChanged.getId(), Mono.just(productChanged), Duration.ofHours(1));
        }

    }

}
