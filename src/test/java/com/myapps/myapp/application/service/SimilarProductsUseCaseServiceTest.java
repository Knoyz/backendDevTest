package com.myapps.myapp.application.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.domain.port.out.CachePort;
import com.myapps.myapp.domain.port.out.EventPublisherPort;
import com.myapps.myapp.domain.port.out.ProductDetailsByIdPort;
import com.myapps.myapp.domain.port.out.SimilarProductsByIdPort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SimilarProductsUseCaseServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private SimilarProductsByIdPort similarProductsByIdPort;

    @Mock
    private ProductDetailsByIdPort productDetailsByIdPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private CachePort cachePort;

    @InjectMocks
    private SimilarProductsUseCaseService service;

    @Test
    void shouldReturnSimilarProducts_whenCacheIsEmpty() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3");
        ProductDetails details2 = new ProductDetails("2", "Product 2", new BigDecimal("10.0"), true);
        ProductDetails details3 = new ProductDetails("3", "Product 3", new BigDecimal("20.0"), true);

        when(cachePort.getCachedSimilarProducts(productId)).thenReturn(Flux.fromIterable(similarIds));
        when(similarProductsByIdPort.getIdsOfSimilarProducts(productId)).thenReturn(Flux.fromIterable(similarIds));

        // Mock cache for product details (simulate cache fetching)
        when(cachePort.getCachedProductDetails("2")).thenReturn(Mono.just(details2));
        when(cachePort.getCachedProductDetails("3")).thenReturn(Mono.just(details3));
        when(productDetailsByIdPort.getProductDetailsById("2")).thenReturn(Mono.empty());
        when(productDetailsByIdPort.getProductDetailsById("3")).thenReturn(Mono.empty());

        StepVerifier.create(service.getSimilarProducts(productId))
                .expectNext(details2)
                .expectNext(details3)
                .verifyComplete();

        verify(similarProductsByIdPort).getIdsOfSimilarProducts(productId);
        verify(productDetailsByIdPort).getProductDetailsById("2");
        verify(productDetailsByIdPort).getProductDetailsById("3");

        verify(cachePort, times(1)).getCachedSimilarProducts(productId);
        verify(cachePort, times(1)).getCachedProductDetails("2");
        verify(cachePort, times(1)).getCachedProductDetails("3");
    }

    @Test
    void shouldReturnFromCache_whenCacheIsPresent_multipleCalls() {
        String productId = "1";
        List<String> cachedIds = List.of("2", "3");
        ProductDetails details2 = new ProductDetails("2", "Product 2", new BigDecimal("10.0"), true);
        ProductDetails details3 = new ProductDetails("3", "Product 3", new BigDecimal("20.0"), true); // external calls

        // Mock cache behavior
        when(cachePort.getCachedSimilarProducts(productId)).thenReturn(Flux.fromIterable(cachedIds));
        when(cachePort.getCachedProductDetails("2")).thenReturn(Mono.just(details2));
        when(cachePort.getCachedProductDetails("3")).thenReturn(Mono.just(details3));

        // Mock external ports to use the mocked WebClient
        when(similarProductsByIdPort.getIdsOfSimilarProducts(anyString())).thenReturn(Flux.empty());
        when(productDetailsByIdPort.getProductDetailsById(anyString())).thenReturn(Mono.empty());

        // Assuming ports use WebClient internally, inject it (if applicable)
        // If your ports have a setter or constructor for WebClient, inject it here
        // Example: ReflectionTestUtils.setField(similarProductsByIdPort, "webClient",
        // webClient);

        // Test multiple calls
        int numberOfCalls = 3;
        for (int i = 0; i < numberOfCalls; i++) {
            StepVerifier.create(service.getSimilarProducts(productId))
                    .expectNext(details2)
                    .expectNext(details3)
                    .verifyComplete();
        }

        // Verify cache interactions
        verify(cachePort, times(numberOfCalls)).getCachedSimilarProducts(productId);
        verify(cachePort, times(numberOfCalls)).getCachedProductDetails("2");
        verify(cachePort, times(numberOfCalls)).getCachedProductDetails("3");

        // Verify WebClient interactions
        verify(webClient, never()).get(); // No HTTP calls should be made
    }

    @Test
    void shouldReturnEmpty_whenNoSimilarProducts() {
        String productId = "1";

        when(cachePort.getCachedSimilarProducts(productId)).thenReturn(Flux.empty());
        when(similarProductsByIdPort.getIdsOfSimilarProducts(productId)).thenReturn(Flux.empty());

        StepVerifier.create(service.getSimilarProducts(productId))
                .verifyComplete();

        verify(cachePort).getCachedSimilarProducts(productId);
        verify(similarProductsByIdPort).getIdsOfSimilarProducts(productId);
        verify(productDetailsByIdPort, never()).getProductDetailsById(anyString());
    }
}