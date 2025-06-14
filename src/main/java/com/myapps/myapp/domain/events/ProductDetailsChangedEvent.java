package com.myapps.myapp.domain.events;

import java.math.BigDecimal;

public record ProductDetailsChangedEvent(
                String id,
                String name,
                BigDecimal price,
                Boolean availability) {
}
