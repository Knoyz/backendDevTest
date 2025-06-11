package com.backendDevTest.myApp.domain.events;

import java.math.BigDecimal;

public record ResponseProductDetailsEvent(
        String id,
        String name,
        BigDecimal price,
        Boolean availability) {
}
