package com.myapps.myapp.domain.port.out;

import reactor.core.publisher.Flux;

public interface SimilarProductsByIdPort {

    /**
     * Retrieves similar products based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar
     *                  products are to be fetched.
     * @return A Flux of ids of similar products.
     */
    Flux<String> getIdsOfSimilarProducts(String productId);
}