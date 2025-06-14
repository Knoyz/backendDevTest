package com.myapps.myapp.infrastructure.adapters.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.domain.port.in.SimilarProductsUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Products", description = "Operations related to products")
@RequiredArgsConstructor
@RestController
public class ProductsController {

    private final SimilarProductsUseCase getSimilarProductsUseCase;

    @Operation(summary = "Get similar products", description = "Retrieves a list of products similar to the specified product ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of similar products retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetails.class))),
            @ApiResponse(responseCode = "404", description = "No similar products found for the specified product ID", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while fetching similar products", content = @Content)
    })
    @GetMapping("product/{id}/similar")
    public Mono<ResponseEntity<Flux<ProductDetails>>> getSimilarProducts(
            @Parameter(description = "ID of the product to find similar products for", required = true) @PathVariable String id) {

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
