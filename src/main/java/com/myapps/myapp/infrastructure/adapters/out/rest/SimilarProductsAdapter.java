package com.myapps.myapp.infrastructure.adapters.out.rest;

import java.time.Duration;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.myapps.myapp.domain.exceptions.ConnectionException;
import com.myapps.myapp.domain.exceptions.ResourceNotFoundException;
import com.myapps.myapp.domain.exceptions.ServiceUnavailableException;
import com.myapps.myapp.domain.port.out.SimilarProductsByIdPort;
import com.myapps.myapp.infrastructure.utils.JsonUtils;

import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

// @Primary
@Slf4j
@Component
public class SimilarProductsAdapter implements SimilarProductsByIdPort {

    private final WebClient webClient;

    public SimilarProductsAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:3001")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(5))))
                .build();
    }

    /**
     * Retrieves similar products based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar
     *                  products are to be fetched.
     * @return A Flux of ids of similar products.
     */
    @Override
    public Flux<String> getSimilarProducts(String productId) {
        log.info("Fetching similar products for productId: {}", productId);

        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ResourceNotFoundException(
                                "Similar products for productId " + productId + " not found")))

                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ServiceUnavailableException("Service unavailable")))

                .bodyToMono(String.class)

                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(WebClientRequestException.class::isInstance))

                .flatMapMany(json -> Flux.fromIterable((JsonUtils
                        .parseJsonToList(json))))

                .onErrorResume(e -> {
                    if (e instanceof TimeoutException) {
                        log.error("Timeout at fetching similar products for ID: {}", productId, e);
                        return Flux.error(new RuntimeException("Timeout at fetching similar products"));

                    } else if (e instanceof WebClientResponseException ex) {
                        if (ex.getStatusCode().is4xxClientError()) {
                            log.warn("Similar products not found for ID: {}", productId);
                            return Flux.empty(); // Devuelve lista vacía para 404

                        } else if (ex.getStatusCode().is5xxServerError()) {
                            log.error("Server error at fetching similar products for ID: {}", productId, e);
                            return Flux.error(new ServiceUnavailableException("Server remote error"));

                        }
                    } else if (e instanceof WebClientRequestException) {
                        log.error("Connection error at fetching similar products for ID: {}", productId, e);
                        return Flux.error(new ConnectionException("Connection error"));

                    }
                    log.error("Error fetching similar products for productId: {}", productId, e);
                    return Flux.error(new RuntimeException("Error fetching similar products", e));

                });
    }

}
