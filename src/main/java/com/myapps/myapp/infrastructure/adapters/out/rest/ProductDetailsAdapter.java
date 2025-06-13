package com.myapps.myapp.infrastructure.adapters.out.rest;

import java.time.Duration;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.domain.port.out.ProductDetailsByIdPort;

import io.netty.handler.timeout.TimeoutException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

// @Primary
@Component
public class ProductDetailsAdapter implements ProductDetailsByIdPort {

    private final WebClient webClient;

    public ProductDetailsAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:3001").build();
    }

    @Override
    public Mono<ProductDetails> getProductDetailsById(String productId) {
        return Mono.defer(() -> webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.empty())
                .bodyToMono(ProductDetails.class)
                .timeout(Duration.ofSeconds(10)) // <- ahora el timeout ocurre dentro del flujo reintentado
        )
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(e -> e instanceof TimeoutException || e instanceof WebClientResponseException.NotFound))
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                .onErrorResume(WebClientResponseException.InternalServerError.class, e -> Mono.empty());
    }

}
