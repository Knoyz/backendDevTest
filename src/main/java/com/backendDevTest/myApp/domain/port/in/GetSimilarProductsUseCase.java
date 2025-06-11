package com.backendDevTest.myApp.domain.port.in;

import com.backendDevTest.myApp.domain.model.ProductDetails;

import reactor.core.publisher.Flux;

public interface GetSimilarProductsUseCase {
    /**
     * Retrieves similar products with details based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar
     *                  products are to be fetched.
     * @return A Flux of ProductDetails containing details of similar products.
     */
    Flux<ProductDetails> getSimilarProducts(String productId);

}
