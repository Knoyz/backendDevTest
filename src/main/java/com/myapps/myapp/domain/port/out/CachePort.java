package com.myapps.myapp.domain.port.out;

import java.time.Duration;

import com.myapps.myapp.domain.model.ProductDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CachePort {
    Flux<String> getCachedSimilarProducts(String productId);

    void cacheSimilarProducts(String productId, Flux<String> similarIds, Duration ttl);

    Mono<ProductDetails> getCachedProductDetails(String productId);

    void cacheProductDetails(String productId, Mono<ProductDetails> productDetails, Duration ttl);
}
