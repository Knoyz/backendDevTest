package com.myapps.myapp.infrastructure.dto;

import java.math.BigDecimal;

public record ProductDetailsDto(
                String id,
                String name,
                BigDecimal price,
                Boolean availability) {
}
