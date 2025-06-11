package com.myapps.myapp.infrastructure.mapper;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.infrastructure.dto.ProductDetailsDto;

public class ProductDtoMapper {

    private ProductDtoMapper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
        // Private constructor to prevent instantiation
    }

    public static ProductDetailsDto fromDomain(ProductDetails productDetails) {
        if (productDetails == null) {
            return null;
        }
        return new ProductDetailsDto(
                productDetails.getId(),
                productDetails.getName(),
                productDetails.getPrice(),
                productDetails.getAvailability());
    }

    public static ProductDetails toDomain(ProductDetailsDto productDetailsDto) {
        if (productDetailsDto == null) {
            return null;
        }
        return new ProductDetails(
                productDetailsDto.id(),
                productDetailsDto.name(),
                productDetailsDto.price(),
                productDetailsDto.availability());
    }
}