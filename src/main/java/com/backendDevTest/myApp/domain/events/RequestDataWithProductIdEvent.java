package com.backendDevTest.myApp.domain.events;

import reactor.core.publisher.Mono;

public record RequestDataWithProductIdEvent(
    Mono<String> id
) {
}
