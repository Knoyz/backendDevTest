package com.backendDevTest.myApp.infrastructure.rest;

import com.backendDevTest.myApp.infrastructure.dto.ProductDetailsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
//@RequestMapping("api/v1/products")
public class ProductsController {

    @GetMapping("/product/{id}/similar")
    public Mono<ResponseEntity<Flux<ProductDetailsDto>>> getSimilarProducts(@PathVariable String id) {

        //:TODO Implement the logic to fetch similar products based on the provided product ID.
        // For now, returning an empty Flux to simulate the response.
        var similarProducts = Flux.<ProductDetailsDto>empty();

        return similarProducts.hasElements()
                .map(hasElements -> hasElements
                        ? ResponseEntity.ok(similarProducts)
                        : ResponseEntity.status(HttpStatus.NOT_FOUND).body(Flux.empty()));
    }


}
