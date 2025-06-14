package com.myapps.myapp.infrastructure.adapters.in.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.myapps.myapp.domain.events.ProductDetailsChangedEvent;
import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.domain.port.out.CachePort;
import com.myapps.myapp.infrastructure.mapper.ProductDetailsEventMapper;

import reactor.core.publisher.Mono;

class KafkaConsumerAdapterTest {

    private CachePort cachePort;
    private KafkaConsumerAdapter kafkaConsumerAdapter;

    @BeforeEach
    void setUp() {
        cachePort = mock(CachePort.class);
        kafkaConsumerAdapter = new KafkaConsumerAdapter(cachePort);
    }

    @SuppressWarnings("unchecked")
    @Test
    void consumeProductDetailsChangedEvent_shouldCacheProductDetails_whenProductChangedIsNotNull() {
        ProductDetailsChangedEvent event = mock(ProductDetailsChangedEvent.class);
        ProductDetails productDetails = mock(ProductDetails.class);
        String productId = "123";

        when(productDetails.getId()).thenReturn(productId);

        try (MockedStatic<ProductDetailsEventMapper> mapperMockedStatic = Mockito
                .mockStatic(ProductDetailsEventMapper.class)) {
            mapperMockedStatic.when(() -> ProductDetailsEventMapper.toDomain(event)).thenReturn(productDetails);

            kafkaConsumerAdapter.consumeProductDetailsChangedEvent(event);

            ArgumentCaptor<Mono<ProductDetails>> monoCaptor = ArgumentCaptor.forClass(Mono.class);
            verify(cachePort).cacheProductDetails(eq(productId), monoCaptor.capture(), eq(Duration.ofHours(1)));

            // Optionally, check that the Mono emits the correct productDetails
            ProductDetails emitted = monoCaptor.getValue().block();
            assertEquals(productDetails, emitted);
        }
    }

    @Test
    void consumeProductDetailsChangedEvent_shouldNotCache_whenProductChangedIsNull() {
        ProductDetailsChangedEvent event = mock(ProductDetailsChangedEvent.class);

        try (MockedStatic<ProductDetailsEventMapper> mapperMockedStatic = Mockito
                .mockStatic(ProductDetailsEventMapper.class)) {
            mapperMockedStatic.when(() -> ProductDetailsEventMapper.toDomain(event)).thenReturn(null);

            kafkaConsumerAdapter.consumeProductDetailsChangedEvent(event);

            verifyNoInteractions(cachePort);
        }
    }
}