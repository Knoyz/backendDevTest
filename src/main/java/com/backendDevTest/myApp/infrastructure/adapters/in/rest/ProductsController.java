package com.backendDevTest.myApp.infrastructure.adapters.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.backendDevTest.myApp.domain.model.ProductDetails;
import com.backendDevTest.myApp.domain.port.in.GetSimilarProductsUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
// @RequestMapping("api/v1/products")
public class ProductsController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    @GetMapping("product/{id}/similar")
    public Mono<ResponseEntity<Flux<ProductDetails>>> getSimilarProducts(@PathVariable String id) {
        return getSimilarProductsUseCase.getSimilarProducts(id)
                .collectList()
                .flatMap(productDetails -> {
                    if (productDetails.isEmpty()) {
                        return Mono.just(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.ok(Flux.fromIterable(productDetails)));
                    }
                });
    }

}
