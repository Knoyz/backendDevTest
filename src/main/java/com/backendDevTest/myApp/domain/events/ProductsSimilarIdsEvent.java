package com.backendDevTest.myApp.domain.events;

import reactor.core.publisher.Flux;

public record ProductsSimilarIdsEvent(
        Flux<String> productIds
) {
}
