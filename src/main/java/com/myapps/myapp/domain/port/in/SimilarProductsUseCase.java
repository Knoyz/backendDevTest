package com.myapps.myapp.domain.port.in;

import com.myapps.myapp.domain.model.ProductDetails;

import reactor.core.publisher.Flux;

public interface SimilarProductsUseCase {
    /**
     * Retrieves similar products with details based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar
     *                  products are to be fetched.
     * @return A Flux of ProductDetails containing details of similar products.
     */
    Flux<ProductDetails> getSimilarProducts(String productId);

}
