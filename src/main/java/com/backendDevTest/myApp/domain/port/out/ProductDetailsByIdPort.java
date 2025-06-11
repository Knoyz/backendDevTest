package com.backendDevTest.myApp.domain.port.out;

import com.backendDevTest.myApp.domain.model.ProductDetails;

import reactor.core.publisher.Mono;

public interface ProductDetailsByIdPort {
    /**
     * Retrieves product details by its unique identifier.
     *
     * @param productId A unique identifier for the product.
     * @return A Mono containing the product details, or empty if not found.
     */
    Mono<ProductDetails> getProductDetailsById(String productId);

}
