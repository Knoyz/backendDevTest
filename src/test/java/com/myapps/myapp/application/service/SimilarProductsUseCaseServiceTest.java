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

    @SuppressWarnings("unchecked")
    @Test
    void shouldFetchAndCacheSimilarProducts_whenNotInCache() {
        String productId = "10";
        List<String> similarIds = List.of("11", "12");
        ProductDetails details11 = new ProductDetails("11", "Product 11", new BigDecimal("100.0"), true);
        ProductDetails details12 = new ProductDetails("12", "Product 12", new BigDecimal("200.0"), true);

        // Cache is empty for similar products
        when(cachePort.getCachedSimilarProducts(productId)).thenReturn(Flux.empty());
        // Fetch similar IDs from port
        when(similarProductsByIdPort.getIdsOfSimilarProducts(productId)).thenReturn(Flux.fromIterable(similarIds));
        // Cache is empty for product details
        when(cachePort.getCachedProductDetails("11")).thenReturn(Mono.empty());
        when(cachePort.getCachedProductDetails("12")).thenReturn(Mono.empty());
        // Fetch product details from port
        when(productDetailsByIdPort.getProductDetailsById("11")).thenReturn(Mono.just(details11));
        when(productDetailsByIdPort.getProductDetailsById("12")).thenReturn(Mono.just(details12));

        StepVerifier.create(service.getSimilarProducts(productId))
                .expectNext(details11)
                .expectNext(details12)
                .verifyComplete();

        verify(cachePort).getCachedSimilarProducts(productId);
        verify(similarProductsByIdPort).getIdsOfSimilarProducts(productId);
        // It verify twice because it's called once in the declaration
        verify(cachePort, times(2)).cacheSimilarProducts(eq(productId), any(), any());
        verify(cachePort).getCachedProductDetails("11");
        verify(cachePort).getCachedProductDetails("12");
        verify(productDetailsByIdPort).getProductDetailsById("11");
        verify(productDetailsByIdPort).getProductDetailsById("12");
        verify(cachePort).cacheProductDetails(eq("11"), any(Mono.class), any());
        verify(cachePort).cacheProductDetails(eq("12"), any(Mono.class), any());
    }

    @Test
    void shouldNotCacheOrFetchDetails_whenSimilarIdIsNull() {
        String productId = "20";
        // Simulate a null similarId
        when(cachePort.getCachedSimilarProducts(productId)).thenReturn(Flux.empty());
        when(similarProductsByIdPort.getIdsOfSimilarProducts(productId)).thenReturn(Flux.empty());

        StepVerifier.create(service.getSimilarProducts(productId))
                .verifyComplete();

        verify(cachePort).getCachedSimilarProducts(productId);
        verify(similarProductsByIdPort).getIdsOfSimilarProducts(productId);
        verify(cachePort, never()).cacheSimilarProducts(anyString(), any(), any());
        verify(cachePort, never()).getCachedProductDetails(anyString());
        verify(productDetailsByIdPort, never()).getProductDetailsById(anyString());
    }

    @Test
    void shouldNotCacheDetails_whenProductDetailsIsNull() {
        String productId = "30";
        String similarId = "31";
        // Cache is empty for similar products
        when(cachePort.getCachedSimilarProducts(productId)).thenReturn(Flux.empty());
        when(similarProductsByIdPort.getIdsOfSimilarProducts(productId)).thenReturn(Flux.just(similarId));
        // Cache is empty for product details
        when(cachePort.getCachedProductDetails(similarId)).thenReturn(Mono.empty());
        // Product details port returns null
        when(productDetailsByIdPort.getProductDetailsById(similarId)).thenReturn(Mono.justOrEmpty(null));

        StepVerifier.create(service.getSimilarProducts(productId))
                .verifyComplete();

        verify(cachePort).getCachedSimilarProducts(productId);
        verify(similarProductsByIdPort).getIdsOfSimilarProducts(productId);
        verify(cachePort).getCachedProductDetails(similarId);
        verify(productDetailsByIdPort).getProductDetailsById(similarId);
        verify(cachePort, never()).cacheProductDetails(anyString(), any(), any());
    }

    @Test
    void fetchProductDetails_shouldReturnFromCache() {
        String similarId = "51";
        ProductDetails details = new ProductDetails("51", "Product 51", new BigDecimal("51.0"), true);

        when(cachePort.getCachedProductDetails(similarId)).thenReturn(Mono.just(details));
        when(productDetailsByIdPort.getProductDetailsById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(service.fetchProductDetails(similarId))
                .expectNext(details)
                .verifyComplete();

        verify(cachePort).getCachedProductDetails(similarId);
        verify(productDetailsByIdPort, times(1)).getProductDetailsById(anyString());
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchProductDetails_shouldFetchAndCache_whenNotInCache() {
        String similarId = "61";
        ProductDetails details = new ProductDetails("61", "Product 61", new BigDecimal("61.0"), true);

        when(cachePort.getCachedProductDetails(similarId)).thenReturn(Mono.empty());
        when(productDetailsByIdPort.getProductDetailsById(similarId)).thenReturn(Mono.just(details));

        StepVerifier.create(service.fetchProductDetails(similarId))
                .expectNext(details)
                .verifyComplete();

        verify(cachePort).getCachedProductDetails(similarId);
        verify(productDetailsByIdPort).getProductDetailsById(similarId);
        verify(cachePort).cacheProductDetails(eq(similarId), any(Mono.class), any());
    }

    @Test
    void fetchProductDetails_shouldNotCache_whenDetailsIsNull() {
        String similarId = "71";

        when(cachePort.getCachedProductDetails(similarId)).thenReturn(Mono.empty());
        when(productDetailsByIdPort.getProductDetailsById(similarId)).thenReturn(Mono.justOrEmpty(null));

        StepVerifier.create(service.fetchProductDetails(similarId))
                .verifyComplete();

        verify(cachePort).getCachedProductDetails(similarId);
        verify(productDetailsByIdPort).getProductDetailsById(similarId);
        verify(cachePort, never()).cacheProductDetails(anyString(), any(), any());
    }

}
