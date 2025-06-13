package com.myapps.myapp.infrastructure.adapters.out.rest;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapps.myapp.domain.model.ProductDetails;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

class ProductDetailsAdapterTest {
    private static MockWebServer mockWebServer;
    private ProductDetailsAdapter productDetailsAdapter;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void init() {
        String baseUrl = String.format("http://localhost:%d", mockWebServer.getPort());
        productDetailsAdapter = new ProductDetailsAdapter(
                WebClient.builder()
                        .baseUrl(baseUrl)
                        .build());
    }

    @Test
    void testFetchProductDetails_success() throws Exception {
        ProductDetails details = new ProductDetails();
        details.setId("123");
        details.setName("Test Product");
        details.setPrice(BigDecimal.valueOf(99.99));
        details.setAvailability(true);

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(details);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(productDetailsAdapter.getProductDetailsById("123"))
                .assertNext(result -> {
                    Assertions.assertNotNull(result);
                    Assertions.assertEquals("123", result.getId());
                    Assertions.assertEquals("Test Product", result.getName());
                    Assertions.assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
                    Assertions.assertTrue(result.getAvailability());
                })
                .verifyComplete();
    }

    @Test
    void testFetchProductDetails_notFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(productDetailsAdapter.getProductDetailsById("notfound"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFetchProductDetails_serverError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(productDetailsAdapter.getProductDetailsById("error"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFetchProductDetails_timeout() {
        mockWebServer.enqueue(new MockResponse()
                .setBodyDelay(10, java.util.concurrent.TimeUnit.SECONDS)
                .setResponseCode(200)
                .setBody("{\"id\":\"timeout\",\"name\":\"Timeout Product\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(productDetailsAdapter.getProductDetailsById("timeout"))
                .expectError(java.util.concurrent.TimeoutException.class)
                .verify();
    }
}
