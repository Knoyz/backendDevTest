package com.myapps.myapp.domain.port.out;

import com.myapps.myapp.domain.model.ProductDetails;

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
