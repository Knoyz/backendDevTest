package com.myapps.myapp.infrastructure.mapper;

import com.myapps.myapp.domain.events.ProductDetailsChangedEvent;
import com.myapps.myapp.domain.model.ProductDetails;

public class ProductDetailsEventMapper {

    private ProductDetailsEventMapper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
        // Private constructor to prevent instantiation
    }

    public static ProductDetails toDomain(ProductDetailsChangedEvent productDetailsChangedEventDto) {

        if (productDetailsChangedEventDto == null) {
            return null;
        }
        return new ProductDetails(
                productDetailsChangedEventDto.id(),
                productDetailsChangedEventDto.name(),
                productDetailsChangedEventDto.price(),
                productDetailsChangedEventDto.availability());
    }

    public static ProductDetailsChangedEvent toDto(ProductDetails productDetails) {
        if (productDetails == null) {
            return null;
        }
        return new ProductDetailsChangedEvent(
                productDetails.getId(),
                productDetails.getName(),
                productDetails.getPrice(),
                productDetails.getAvailability());
    }
}
