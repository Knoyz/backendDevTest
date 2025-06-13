package com.myapps.myapp.application.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.domain.port.in.SimilarProductsUseCase;
import com.myapps.myapp.domain.port.out.CachePort;
import com.myapps.myapp.domain.port.out.EventPublisherPort;
import com.myapps.myapp.domain.port.out.ProductDetailsByIdPort;
import com.myapps.myapp.domain.port.out.SimilarProductsByIdPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class SimilarProductsUseCaseService implements SimilarProductsUseCase {

        private final SimilarProductsByIdPort similarProductsByIdPort;
        private final ProductDetailsByIdPort productDetailsByIdPort;
        private final EventPublisherPort eventPublisherPort;
        private final CachePort cachePort;

        private static final Duration TTL = Duration.ofHours(1);
        private static final String SIMILAR_PRODUCTS_IDS = "similar_products_ids";
        private static final String SIMILAR_PRODUCTS_DETAILS_FETCH = "similar_products_details_fetch";

        /**
         * Retrieves similar products with details based on the provided product ID.
         *
         * @param productId A unique identifier for the product for which similar
         *                  products are to be fetched.
         * @return A Flux of ProductDetails containing details of similar products.
         */
        @Override
        public Flux<ProductDetails> getSimilarProducts(String productId) {
                // eventPublisherPort.publishGetSimilarProductsEvent(SIMILAR_PRODUCTS_IDS,
                // productId);

                return cachePort.getCachedSimilarProducts(productId)
                                .switchIfEmpty(
                                                similarProductsByIdPort.getSimilarProducts(productId)
                                                                .doOnNext(similarId -> {
                                                                        if (similarId == null) {
                                                                                log.warn("No similar products found for product ID: {}",
                                                                                                productId);
                                                                                return;
                                                                        } else {
                                                                                log.info("Caching similar product ID: {} for product ID: {}",
                                                                                                similarId, productId);
                                                                                cachePort.cacheSimilarProducts(
                                                                                                productId,
                                                                                                Flux.just(similarId),
                                                                                                TTL);
                                                                        }
                                                                }))
                                .flatMap(this::fetchProductDetails)
                                .filter(details -> details != null && details.getId() != null);
                // .doOnNext(details -> eventPublisherPort.publishFetchProductDetailsEvent(
                // SIMILAR_PRODUCTS_DETAILS_FETCH, JsonUtils.toJson(details)));
        }

        /**
         * Fetches product details for a given similar product ID.
         *
         * @param similarProductId A unique identifier for the similar product.
         * @return A Mono containing the product details, or empty if not found.
         */
        private Mono<ProductDetails> fetchProductDetails(String similarId) {

                return cachePort.getCachedProductDetails(similarId)
                                .switchIfEmpty(
                                                productDetailsByIdPort.getProductDetailsById(similarId)
                                                                .doOnNext(details -> {
                                                                        if (details == null
                                                                                        || details.getId() == null) {
                                                                                log.warn("No details found for similar product ID: {}",
                                                                                                similarId);
                                                                        } else {
                                                                                cachePort.cacheProductDetails(
                                                                                                similarId,
                                                                                                Mono.just(details),
                                                                                                TTL);
                                                                        }
                                                                }));
        }
}
