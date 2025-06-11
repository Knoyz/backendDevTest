package com.backendDevTest.myApp.application.usecase;

import com.backendDevTest.myApp.domain.model.ProductDetails;
import reactor.core.publisher.Flux;

public interface ObtainSimilarProductsDetailsFromProductIdUseCase {

    Flux<ProductDetails> getSimilarProducts(String productId);
}
