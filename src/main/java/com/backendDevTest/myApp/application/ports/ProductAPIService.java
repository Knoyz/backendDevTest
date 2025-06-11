package com.backendDevTest.myApp.application.ports;

import com.backendDevTest.myApp.domain.model.ProductDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductAPIService {

    Mono<ProductDetails> getProductDetails(String productId);
    Flux<String> getSimilarProductIds(String productId);
}
