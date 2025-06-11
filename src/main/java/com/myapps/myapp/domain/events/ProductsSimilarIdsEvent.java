package com.myapps.myapp.domain.events;

import reactor.core.publisher.Flux;

public record ProductsSimilarIdsEvent(
        Flux<String> productIds) {
}
