package com.myapps.myapp.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.infrastructure.dto.ProductDetailsDto;

class ProductDtoMapperTest {
    @Test
    void shouldMapDomainToDto() {
        ProductDetails productDetails = new ProductDetails("1", "Test Product", BigDecimal.valueOf(9.99), true);

        ProductDetailsDto dto = ProductDtoMapper.fromDomain(productDetails);

        assertNotNull(dto);
        assertEquals(productDetails.getId(), dto.id());
        assertEquals(productDetails.getName(), dto.name());
        assertEquals(productDetails.getPrice(), dto.price());
        assertEquals(productDetails.getAvailability(), dto.availability());
    }

    @Test
    void shouldMapDtoToDomain() {
        ProductDetailsDto dto = new ProductDetailsDto("2", "Another Product", BigDecimal.valueOf(19.99), false);

        ProductDetails productDetails = ProductDtoMapper.toDomain(dto);

        assertNotNull(productDetails);
        assertEquals(dto.id(), productDetails.getId());
        assertEquals(dto.name(), productDetails.getName());
        assertEquals(dto.price(), productDetails.getPrice());
        assertEquals(dto.availability(), productDetails.getAvailability());
    }

    @Test
    void fromDomainShouldReturnNullWhenInputIsNull() {
        assertNull(ProductDtoMapper.fromDomain(null));
    }

    @Test
    void toDomainShouldReturnNullWhenInputIsNull() {
        assertNull(ProductDtoMapper.toDomain(null));
    }
}
