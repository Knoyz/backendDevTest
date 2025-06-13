package com.myapps.myapp.domain.port.in;

import com.myapps.myapp.domain.model.ProductDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SimilarProductsUseCase {
    /**
     * Retrieves similar products with details based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar
     *                  products are to be fetched.
     * @return A Flux of ProductDetails containing details of similar products.
     */
    Flux<ProductDetails> getSimilarProducts(String productId);

    /**
     * Fetches product details by its unique identifier.
     *
     * @param productId A unique identifier for the product.
     * @return A Mono containing the product details, or empty if not found.
     */
    Mono<ProductDetails> fetchProductDetails(String productId);

}
