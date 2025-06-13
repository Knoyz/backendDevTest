package com.myapps.myapp.infrastructure.adapters.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.myapps.myapp.domain.port.in.SimilarProductsUseCase;

@WebFluxTest(ProductsController.class)
class ProductsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SimilarProductsUseCase similarProductsUseCase;

    @Test
    void shouldReturn200_whenSimilarProductsFound() {
        // TODO: Implementar test
    }

    @Test
    void shouldReturn404_whenNoSimilarProducts() {
        // TODO: Implementar test
    }
}