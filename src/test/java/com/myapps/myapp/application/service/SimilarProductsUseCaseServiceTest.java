package com.myapps.myapp.application.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private SimilarProductsByIdPort similarProductsByIdPort;
    @Mock
    private ProductDetailsByIdPort productDetailsByIdPort;
    @Mock
    private EventPublisherPort eventPublisherPort;
    @Mock
    private CachePort cachePort;

    @InjectMocks
    private SimilarProductsUseCaseService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnSimilarProducts_whenCacheIsEmpty() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3");
        ProductDetails details2 = new ProductDetails("2", "Product 2", new BigDecimal("10.0"), true);
        ProductDetails details3 = new ProductDetails("3", "Product 3", new BigDecimal("20.0"), true);

        when(cachePort.getCachedSimilarProducts(productId)).thenReturn(Flux.empty());
        when(similarProductsByIdPort.getIdsOfSimilarProducts(productId)).thenReturn(Flux.fromIterable(similarIds));
        // Mock cache for product details (simulate cache miss)
        when(cachePort.getCachedProductDetails("2")).thenReturn(Mono.empty());
        when(cachePort.getCachedProductDetails("3")).thenReturn(Mono.empty());
        when(productDetailsByIdPort.getProductDetailsById("2")).thenReturn(Mono.just(details2));
        when(productDetailsByIdPort.getProductDetailsById("3")).thenReturn(Mono.just(details3));

        StepVerifier.create(service.getSimilarProducts(productId))
                .expectNext(details2)
                .expectNext(details3)
                .verifyComplete();

        verify(cachePort).getCachedSimilarProducts(productId);
        verify(similarProductsByIdPort).getIdsOfSimilarProducts(productId);
        verify(cachePort).getCachedProductDetails("2");
        verify(cachePort).getCachedProductDetails("3");
        verify(productDetailsByIdPort).getProductDetailsById("2");
        verify(productDetailsByIdPort).getProductDetailsById("3");
    }

    @Test
    void shouldReturnFromCache_whenCacheIsPresent() {
        String productId = "1";
        List<String> cachedIds = List.of("2", "3");
        ProductDetails details2 = new ProductDetails("2", "Product 2", new BigDecimal("10.0"), true);
        ProductDetails details3 = new ProductDetails("3", "Product 3", new BigDecimal("20.0"), true);

        // El caché devuelve los IDs similares
        when(cachePort.getCachedSimilarProducts(productId)).thenReturn(Flux.fromIterable(cachedIds));
        // El caché devuelve los detalles de producto para cada ID
        when(cachePort.getCachedProductDetails("2")).thenReturn(Mono.just(details2));
        when(cachePort.getCachedProductDetails("3")).thenReturn(Mono.just(details3));
        // Por seguridad, si se llama a productDetailsByIdPort, que devuelva vacío
        when(productDetailsByIdPort.getProductDetailsById(anyString())).thenReturn(Mono.empty());
        // Por seguridad, si se llama a similarProductsByIdPort, que devuelva vacío
        when(similarProductsByIdPort.getIdsOfSimilarProducts(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(service.getSimilarProducts(productId))
                .expectNext(details2)
                .expectNext(details3)
                .verifyComplete();

        verify(cachePort).getCachedSimilarProducts(productId);
        verify(cachePort).getCachedProductDetails("2");
        verify(cachePort).getCachedProductDetails("3");
        // Verifica que NO se llama al puerto de productos similares
        verify(similarProductsByIdPort, never()).getIdsOfSimilarProducts(anyString());
        // Verifica que NO se llama al puerto de detalles externos
        verify(productDetailsByIdPort, never()).getProductDetailsById(anyString());
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