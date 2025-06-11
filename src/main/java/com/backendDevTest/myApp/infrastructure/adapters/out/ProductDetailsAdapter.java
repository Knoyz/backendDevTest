package com.backendDevTest.myApp.infrastructure.adapters.out;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.backendDevTest.myApp.domain.model.ProductDetails;
import com.backendDevTest.myApp.domain.port.out.ProductDetailsByIdPort;

import reactor.core.publisher.Mono;

@Component
public class ProductDetailsAdapter implements ProductDetailsByIdPort {

    private final WebClient webClient;

    public ProductDetailsAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:3001").build();
    }

    @Override
    public Mono<ProductDetails> getProductDetailsById(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductDetails.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error fetching product details", e)));
    }

}
