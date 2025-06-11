package com.backendDevTest.myApp.application.service;

import com.backendDevTest.myApp.application.usecase.ObtainSimilarProductsDetailsFromProductIdUseCase;
import com.backendDevTest.myApp.domain.model.ProductDetails;
import com.backendDevTest.myApp.infrastructure.adapters.out.KafkaAdapterProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class ProductServiceUseCase implements ObtainSimilarProductsDetailsFromProductIdUseCase {

    private final KafkaAdapterProducer kafkaAdapterProducer;

    /**
     * Retrieves similar products with details based on the provided product ID.
     *
     * @param productId A unique identifier for the product for which similar products are to be fetched.
     * @return A Flux of ProductDetails containing details of similar products.
     */
    @Override
    public Flux<ProductDetails> getSimilarProducts(String productId) {

        return null;
    }
}
