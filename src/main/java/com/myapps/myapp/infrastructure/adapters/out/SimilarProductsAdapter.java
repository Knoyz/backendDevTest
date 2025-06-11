package com.myapps.myapp.infrastructure.adapters.out;

import java.util.Arrays;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.myapps.myapp.domain.port.out.SimilarProductsByIdPort;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class SimilarProductsAdapter implements SimilarProductsByIdPort {

    private final WebClient webClient;

    public SimilarProductsAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:3001").build();
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
                .bodyToMono(String.class)
                .flatMapMany(json -> Flux.fromIterable(Arrays.asList(json
                        .strip()
                        .replace("[", "")
                        .replace("]", "")
                        .split(","))))
                .onErrorResume(e -> Flux.error(new RuntimeException("Error fetching similar products", e)));
    }

}
