package com.backendDevTest.myApp.application.service;

import com.backendDevTest.myApp.application.ports.ProductAPIService;
import com.backendDevTest.myApp.application.usecase.ObtainSimilarProductsDetailsFromProductId;
import com.backendDevTest.myApp.domain.model.ProductDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService implements ProductAPIService, ObtainSimilarProductsDetailsFromProductId {

    /**
     * Retrieves product details based on the provided product ID.
     *
     * @param productId A unique identifier for the product.
     * @return A Mono containing the ProductDetails for the specified product ID.
     */
    @Override
    public Mono<ProductDetails> getProductDetails(String productId) {
        return null;
    }

    /**
     * Retrieves similar product IDs based on the provided product ID.
     *
     * @param productId A unique identifier for the product.
     * @return A Flux containing the IDs of similar products.
     */
    @Override
    public Flux<String> getSimilarProductIds(String productId) {
        return null;
    }

    /**
     * Retrieves similar products with details based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar products are to be fetched.
     * @return A Flux of ProductDetails containing details of similar products.
     */
    @Override
    public Flux<ProductDetails> getSimilarProducts(String productId) {

        var similarProductIds = getSimilarProductIds(productId);
        if (similarProductIds != null) {
            return similarProductIds.flatMap(this::getProductDetails);

        }
        return null;
    }
}
