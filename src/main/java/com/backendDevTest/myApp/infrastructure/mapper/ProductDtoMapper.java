package com.backendDevTest.myApp.infrastructure.mapper;

import com.backendDevTest.myApp.domain.model.ProductDetails;
import com.backendDevTest.myApp.infrastructure.dto.ProductDetailsDto;

public class ProductDtoMapper {

    public static ProductDetailsDto fromDomain(ProductDetails productDetails) {
        if (productDetails == null) {
            return null;
        }
        return new ProductDetailsDto(
                productDetails.getId(),
                productDetails.getName(),
                productDetails.getPrice(),
                productDetails.getAvailability()
        );
    }

    public static ProductDetails toDomain(ProductDetailsDto productDetailsDto) {
        if (productDetailsDto == null) {
            return null;
        }
        return new ProductDetails(
                productDetailsDto.id(),
                productDetailsDto.name(),
                productDetailsDto.price(),
                productDetailsDto.availability()
        );
    }
}