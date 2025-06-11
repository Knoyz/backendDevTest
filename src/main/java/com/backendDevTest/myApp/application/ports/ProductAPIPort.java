package com.backendDevTest.myApp.application.ports;

import reactor.core.publisher.Mono;

public interface ProductAPIPort {

    Mono<Void> requestProductDetails(String productId);

    Mono<Void> requestSimilarProductIds(String productId);
}
