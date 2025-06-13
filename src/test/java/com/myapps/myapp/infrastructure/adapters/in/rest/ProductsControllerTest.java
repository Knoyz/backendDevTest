package com.myapps.myapp.infrastructure.adapters.in.rest;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.domain.port.in.SimilarProductsUseCase;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest(ProductsController.class)
class ProductsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SimilarProductsUseCase similarProductsUseCase;

    @Test
    void shouldReturn200_whenSimilarProductsFound() {
        String productId = "1";
        ProductDetails similarProduct1 = new ProductDetails("2", "Product 2", BigDecimal.valueOf(10.0), true);
        ProductDetails similarProduct2 = new ProductDetails("3", "Product 3", BigDecimal.valueOf(15.0), true);

        when(similarProductsUseCase.getSimilarProducts(productId))
                .thenReturn(Flux.just(similarProduct1, similarProduct2));

        webTestClient.get()
                .uri("/product/" + productId + "/similar")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDetails.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectNext(similarProduct1)
                .expectNext(similarProduct2)
                .verifyComplete();
    }

    @Test
    void shouldReturn404_whenNoSimilarProducts() {
        String productId = "3513";

        when(similarProductsUseCase.getSimilarProducts(productId))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/product/" + productId + "/similar")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn5xx_whenErrorOccurs() {
        String productId = "3513";

        when(similarProductsUseCase.getSimilarProducts(productId))
                .thenReturn(Flux.error(new RuntimeException("Something went wrong")));

        webTestClient.get()
                .uri("/product/" + productId + "/similar")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}