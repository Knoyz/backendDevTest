package com.myapps.myapp.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.myapps.myapp.domain.events.ProductDetailsChangedEvent;
import com.myapps.myapp.domain.model.ProductDetails;

class ProductDetailsEventMapperTest {

    @Test
    void testToDomain_WithValidEvent_ReturnsProductDetails() {
        ProductDetailsChangedEvent event = new ProductDetailsChangedEvent(
                "prod-1", "Product Name", BigDecimal.valueOf(99.99), true);

        ProductDetails details = ProductDetailsEventMapper.toDomain(event);

        assertNotNull(details);
        assertEquals("prod-1", details.getId());
        assertEquals("Product Name", details.getName());
        assertEquals(BigDecimal.valueOf(99.99), details.getPrice());
        assertTrue(details.getAvailability());
    }

    @Test
    void testToDomain_WithNullEvent_ReturnsNull() {
        assertNull(ProductDetailsEventMapper.toDomain(null));
    }

    @Test
    void testToDto_WithValidProductDetails_ReturnsEvent() {
        ProductDetails details = new ProductDetails(
                "prod-2", "Another Product", BigDecimal.valueOf(49.50), false);

        ProductDetailsChangedEvent event = ProductDetailsEventMapper.toDto(details);

        assertNotNull(event);
        assertEquals("prod-2", event.id());
        assertEquals("Another Product", event.name());
        assertEquals(BigDecimal.valueOf(49.50), event.price());
        assertFalse(event.availability());
    }

    @Test
    void testToDto_WithNullProductDetails_ReturnsNull() {
        assertNull(ProductDetailsEventMapper.toDto(null));
    }

}
