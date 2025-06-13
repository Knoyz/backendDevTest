package com.myapps.myapp.infrastructure.adapters.out.rest;

import java.time.Duration;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.myapps.myapp.domain.port.out.SimilarProductsByIdPort;
import com.myapps.myapp.infrastructure.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

// @Primary
@RequiredArgsConstructor
@Slf4j
@Component
public class SimilarProductsAdapter implements SimilarProductsByIdPort {

    private final WebClient webClient;

    /**
     * Retrieves similar products based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar
     *                  products are to be fetched.
     * @return A Flux of ids of similar products.
     */
    @Override
    public Flux<String> getIdsOfSimilarProducts(String productId) {
        log.info("Fetching similar products for productId: {}", productId);

        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.empty())

                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.empty())

                .bodyToMono(String.class)

                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(WebClientRequestException.class::isInstance))

                .flatMapMany(json -> Flux.fromIterable((JsonUtils
                        .parseJsonToList(json))))

                .onErrorResume(WebClientResponseException.NotFound.class, e -> Flux.empty())
                .onErrorResume(WebClientResponseException.InternalServerError.class, e -> Flux.empty());
    }

}
