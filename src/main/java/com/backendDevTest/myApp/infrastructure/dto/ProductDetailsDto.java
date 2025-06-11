package com.backendDevTest.myApp.infrastructure.dto;

import java.math.BigDecimal;

public record ProductDetailsDto(
    String id,
    String name,
    BigDecimal price,
    Boolean availability
) {
}
