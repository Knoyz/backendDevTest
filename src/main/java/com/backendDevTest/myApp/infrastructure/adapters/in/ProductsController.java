package com.backendDevTest.myApp.infrastructure.adapters.in;

import com.backendDevTest.myApp.domain.model.ProductDetails;
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
    public Mono<ResponseEntity<Flux<ProductDetails>>> getSimilarProducts(@PathVariable String id) {

        //:TODO Implement the logic to fetch similar products based on the provided product ID.
        // For now, returning an empty Flux to simulate the response.
        Flux<ProductDetails> similarProducts = Flux.empty();

        return similarProducts.hasElements()
                .map(hasElements -> hasElements
                        ? ResponseEntity.ok(similarProducts)
                        : ResponseEntity.status(HttpStatus.NOT_FOUND).body(Flux.empty()));
    }


}
