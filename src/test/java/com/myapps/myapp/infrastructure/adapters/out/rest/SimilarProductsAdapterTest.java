package com.myapps.myapp.infrastructure.adapters.out.rest;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.mockwebserver.MockWebServer;

class SimilarProductsAdapterTest {

    private static MockWebServer mockWebServer;
    @SuppressWarnings("unused")
    private SimilarProductsAdapter similarProductsAdapter;

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
        similarProductsAdapter = new SimilarProductsAdapter(
                WebClient.builder()
                        .baseUrl(baseUrl)
                        .build());
    }

    @Test
    void shouldReturnSimilarProductIds_whenResponseIsOk() {
        String responseBody = "[\"2\",\"3\",\"4\"]";
        mockWebServer.enqueue(new okhttp3.mockwebserver.MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        SimilarProductsAdapter adapter = new SimilarProductsAdapter(
                WebClient.builder().baseUrl("http://localhost:" + mockWebServer.getPort()).build());

        var result = adapter.getIdsOfSimilarProducts("1").collectList().block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("\"2\"", result.get(0));
        Assertions.assertEquals("\"3\"", result.get(1));
        Assertions.assertEquals("\"4\"", result.get(2));
    }

    @Test
    void shouldReturnEmpty_whenNotFound() {
        mockWebServer.enqueue(new okhttp3.mockwebserver.MockResponse()
                .setResponseCode(404));

        SimilarProductsAdapter adapter = new SimilarProductsAdapter(
                WebClient.builder().baseUrl("http://localhost:" + mockWebServer.getPort()).build());

        var result = adapter.getIdsOfSimilarProducts("notfound").collectList().block();

        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenInternalServerError() {
        mockWebServer.enqueue(new okhttp3.mockwebserver.MockResponse()
                .setResponseCode(500));

        SimilarProductsAdapter adapter = new SimilarProductsAdapter(
                WebClient.builder().baseUrl("http://localhost:" + mockWebServer.getPort()).build());

        var result = adapter.getIdsOfSimilarProducts("error").collectList().block();

        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void shouldTimeout_whenServerDoesNotRespond() {
        mockWebServer.enqueue(new okhttp3.mockwebserver.MockResponse()
                .setBodyDelay(10, java.util.concurrent.TimeUnit.SECONDS)
                .setBody("[\"1\"]")
                .setResponseCode(200));

        SimilarProductsAdapter adapter = new SimilarProductsAdapter(
                WebClient.builder().baseUrl("http://localhost:" + mockWebServer.getPort()).build());

        var flux = adapter.getIdsOfSimilarProducts("timeout");
        Assertions.assertThrows(RuntimeException.class, flux::blockFirst);
    }

}
