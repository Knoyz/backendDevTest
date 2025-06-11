package com.backendDevTest.myApp.infrastructure.rest;

import com.backendDevTest.myApp.application.usecase.ObtainSimilarProductsDetailsFromProductId;
import com.backendDevTest.myApp.infrastructure.dto.ProductDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
//@RequestMapping("api/v1/products")
public class ProductsController {

    private final ObtainSimilarProductsDetailsFromProductId obtainSimilarProductsDetailsFromProductId;

    @GetMapping("/product/{id}/similar")
    public Mono<ResponseEntity<Flux<ProductDetailsDto>>> getSimilarProducts(@PathVariable String id) {

        var similarProducts = obtainSimilarProductsDetailsFromProductId.getSimilarProducts(id);

        return similarProducts.hasElements().map( hasElements -> {
            if (hasElements) {
                return ResponseEntity.ok(similarProducts.map(
                        product -> new ProductDetailsDto(
                                product.getId(),
                                product.getName(),
                                product.getPrice(),
                                product.getAvailability()
                        )
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Flux.empty());
            }
        });
    }


}
