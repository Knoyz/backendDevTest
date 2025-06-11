package com.backendDevTest.myApp.application.service;

import org.springframework.stereotype.Service;

import com.backendDevTest.myApp.domain.model.ProductDetails;
import com.backendDevTest.myApp.domain.port.in.GetSimilarProductsUseCase;
import com.backendDevTest.myApp.domain.port.out.EventPublisherPort;
import com.backendDevTest.myApp.domain.port.out.ProductDetailsByIdPort;
import com.backendDevTest.myApp.domain.port.out.SimilarProductsByIdPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductService implements GetSimilarProductsUseCase {

    private static final String SIMILAR_PRODUCTS_IDS = "SIMILAR_PRODUCTS_IDS";
    private static final String SIMILAR_PRODUCTS_DETAILS_FETCHED = "SIMILAR_PRODUCTS_DETAILS_FETCHED";

    private final SimilarProductsByIdPort similarProductsByIdPort;
    private final ProductDetailsByIdPort productDetailsByIdPort;
    private final EventPublisherPort eventPublisherPort;

    /**
     * Retrieves similar products with details based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar
     *                  products are to be fetched.
     * @return A Flux of ProductDetails containing details of similar products.
     */
    @Override
    public Flux<ProductDetails> getSimilarProducts(String productId) {

        eventPublisherPort.publishEvent(SIMILAR_PRODUCTS_IDS, productId);

        return similarProductsByIdPort.getSimilarProducts(productId)
                .flatMap(this::fetchProductDetails)
                .doOnComplete(() -> eventPublisherPort.publishEvent(SIMILAR_PRODUCTS_DETAILS_FETCHED, productId));

    }

    /**
     * Fetches product details for a given similar product ID.
     *
     * @param similarProductId A unique identifier for the similar product.
     * @return A Mono containing the product details, or empty if not found.
     */
    private Mono<ProductDetails> fetchProductDetails(String similarProductId) {
        return productDetailsByIdPort.getProductDetailsById(similarProductId);

    }
}
