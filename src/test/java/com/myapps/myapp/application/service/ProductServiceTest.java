package com.myapps.myapp.application.service;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.domain.port.out.EventPublisherPort;
import com.myapps.myapp.domain.port.out.ProductDetailsByIdPort;
import com.myapps.myapp.domain.port.out.SimilarProductsByIdPort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

        private static final String SIMILAR_PRODUCTS_IDS = "SIMILAR_PRODUCTS_IDS";
        private static final String SIMILAR_PRODUCTS_DETAILS_FETCHED = "SIMILAR_PRODUCTS_DETAILS_FETCHED";

        @Mock
        private SimilarProductsByIdPort similarProductsByIdPort;

        @Mock
        private ProductDetailsByIdPort productDetailsByIdPort;

        @Mock
        private EventPublisherPort eventPublisherPort;

        @InjectMocks
        private ProductService productService;

        @BeforeEach
        void setUp() {
                // Configurar comportamiento por defecto de los mocks si es necesario
        }

        @Test
        void getSimilarProducts_successfulCase_returnsProductDetails() {
                // Arrange
                String productId = "123";
                String[] similarIds = { "456", "789" };
                ProductDetails product1 = new ProductDetails("456", "Product A", BigDecimal.valueOf(10.0), false);
                ProductDetails product2 = new ProductDetails("789", "Product B", BigDecimal.valueOf(20.0), true);

                // Configurar mocks
                when(similarProductsByIdPort.getSimilarProducts(productId))
                                .thenReturn(Flux.just(similarIds));
                when(productDetailsByIdPort.getProductDetailsById("456"))
                                .thenReturn(Mono.just(product1));
                when(productDetailsByIdPort.getProductDetailsById("789"))
                                .thenReturn(Mono.just(product2));

                // Act
                Flux<ProductDetails> result = productService.getSimilarProducts(productId);

                // Assert
                StepVerifier.create(result)
                                .expectNext(product1)
                                .expectNext(product2)
                                .verifyComplete();

                // Verificar interacciones con los mocks
                verify(eventPublisherPort).publishEvent(SIMILAR_PRODUCTS_IDS, productId);
                verify(similarProductsByIdPort).getSimilarProducts(productId);
                verify(productDetailsByIdPort).getProductDetailsById("456");
                verify(productDetailsByIdPort).getProductDetailsById("789");
                verify(eventPublisherPort).publishEvent(SIMILAR_PRODUCTS_DETAILS_FETCHED, productId);
                verifyNoMoreInteractions(eventPublisherPort, similarProductsByIdPort, productDetailsByIdPort);
        }

        @Test
        void getSimilarProducts_emptySimilarIds_returnsEmptyFlux() {
                // Arrange
                String productId = "123";

                // Configurar mocks
                when(similarProductsByIdPort.getSimilarProducts(productId))
                                .thenReturn(Flux.empty());

                // Act
                Flux<ProductDetails> result = productService.getSimilarProducts(productId);

                // Assert
                StepVerifier.create(result)
                                .expectComplete()
                                .verify();

                // Verificar interacciones
                verify(eventPublisherPort).publishEvent(SIMILAR_PRODUCTS_IDS, productId);
                verify(similarProductsByIdPort).getSimilarProducts(productId);
                verify(eventPublisherPort).publishEvent(SIMILAR_PRODUCTS_DETAILS_FETCHED, productId);
                verifyNoMoreInteractions(eventPublisherPort, similarProductsByIdPort);
                verifyNoInteractions(productDetailsByIdPort);
        }

        @Test
        void getSimilarProducts_errorInSimilarProducts_throwsException() {
                // Arrange
                String productId = "123";
                RuntimeException error = new RuntimeException("Error fetching similar products");

                // Configurar mocks
                when(similarProductsByIdPort.getSimilarProducts(productId))
                                .thenReturn(Flux.error(error));

                // Act
                Flux<ProductDetails> result = productService.getSimilarProducts(productId);

                // Assert
                StepVerifier.create(result)
                                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                                                && throwable.getMessage().equals("Error fetching similar products"))
                                .verify();

                // Verificar interacciones
                verify(eventPublisherPort).publishEvent(SIMILAR_PRODUCTS_IDS, productId);
                verify(similarProductsByIdPort).getSimilarProducts(productId);
                verifyNoMoreInteractions(eventPublisherPort, similarProductsByIdPort);
                verifyNoInteractions(productDetailsByIdPort);
        }

        @Test
        void getSimilarProducts_errorInProductDetails_propagatesError() {
                // Arrange
                String productId = "123";
                String similarId = "456";
                RuntimeException error = new RuntimeException("Error fetching product details");

                // Configurar mocks
                when(similarProductsByIdPort.getSimilarProducts(productId))
                                .thenReturn(Flux.just(similarId));
                when(productDetailsByIdPort.getProductDetailsById(similarId))
                                .thenReturn(Mono.error(error));

                // Act
                Flux<ProductDetails> result = productService.getSimilarProducts(productId);

                // Assert
                StepVerifier.create(result)
                                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                                                && throwable.getMessage().equals("Error fetching product details"))
                                .verify();

                // Verificar interacciones
                verify(eventPublisherPort).publishEvent(SIMILAR_PRODUCTS_IDS, productId);
                verify(similarProductsByIdPort).getSimilarProducts(productId);
                verify(productDetailsByIdPort).getProductDetailsById(similarId);
                verifyNoMoreInteractions(eventPublisherPort, similarProductsByIdPort, productDetailsByIdPort);
        }
}
