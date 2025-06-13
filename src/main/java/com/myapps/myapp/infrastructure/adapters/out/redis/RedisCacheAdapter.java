package com.myapps.myapp.infrastructure.adapters.out.redis;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import com.myapps.myapp.domain.model.ProductDetails;
import com.myapps.myapp.domain.port.out.CachePort;

import io.lettuce.core.RedisCommandExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheAdapter implements CachePort {

    private final ReactiveRedisTemplate<String, List<String>> stringListRedisTemplate;
    private final ReactiveRedisTemplate<String, ProductDetails> productDetailsRedisTemplate;
    private static final String SIMILAR_IDS_PREFIX = "similar-product-ids-of:";
    private static final String DETAILS_PREFIX = "product-details:";
    private static final Duration TTL = Duration.ofHours(1); // 1-hour TTL

    @Override
    public Flux<String> getCachedSimilarProducts(String productId) {
        String key = SIMILAR_IDS_PREFIX + productId;
        log.info("Checking cache for similar IDs key: {}", key);

        return stringListRedisTemplate.opsForList()
                .range(key, 0, -1) // Retrieves all elements in the list
                .flatMap(k -> {
                    if (k == null || k.isEmpty()) {
                        log.info("Cache miss or empty for key: {}", key);
                        return Flux.empty();
                    }

                    // Flatten the list of lists into a single Flux of strings
                    return Flux.fromIterable(k);
                });
    }

    @Override
    public void cacheSimilarProducts(String productId, Flux<String> similarIds, Duration ttl) {
        String key = SIMILAR_IDS_PREFIX + productId;
        log.debug("Caching similar products IDs for key: {}", key);
        if (similarIds == null) {
            log.warn("Attempted to cache null similar IDs for key: {}", key);
            return;
        }

        similarIds
                .doOnNext(id -> {
                    stringListRedisTemplate.opsForList().rightPush(key, List.of(id))
                            .doOnSuccess(result -> log.debug("Appended similar product ID {} to key: {}", id, key))
                            .doOnError(e -> log.error("Error appending similar product ID {} to key: {}", id, key, e))
                            .subscribe();
                })
                .doOnComplete(() -> {
                    stringListRedisTemplate.expire(key, ttl == null ? TTL : ttl)
                            .doOnSuccess(result -> log.debug("Set TTL for key: {}", key))
                            .doOnError(e -> log.error("Error setting TTL for key: {}", key, e))
                            .subscribe();
                })
                .subscribe();
    }

    @Override
    public Mono<ProductDetails> getCachedProductDetails(String productId) {
        String key = DETAILS_PREFIX + productId;
        log.info("Checking cache for product details key: {}", key);
        return productDetailsRedisTemplate.opsForValue()
                .get(key)
                .flatMap(details -> {
                    if (details.getId() == null) {
                        log.info("Cache miss for product details key: {}", key);
                        return Mono.empty();
                    }
                    log.info("Cache hit for product details key: {}, details: {}", key, details);
                    return Mono.just(details);
                })
                .switchIfEmpty(Mono.empty())
                .doOnSubscribe(subscription -> log.info("Subscribed to cache for product details key: {}", key))
                .onErrorResume(e -> {
                    if (e instanceof RedisCommandExecutionException) {
                        log.error("Redis command execution error for key: {}", key, e);
                        return Mono.empty();

                    }
                    log.error("Error retrieving cached product details for key: {}", key, e);
                    return Mono.empty();
                });
    }

    @Override
    public void cacheProductDetails(String productId, Mono<ProductDetails> productDetails, Duration ttl) {
        String key = DETAILS_PREFIX + productId;
        log.debug("Caching product details for key: {}", key);
        productDetails
                .flatMap(details -> {
                    if (details == null) {
                        log.warn("Attempted to cache null product details for key: {}", key);
                        return Mono.empty();
                    }
                    return productDetailsRedisTemplate.opsForValue()
                            .set(key, details, ttl == null ? TTL : ttl);
                })
                .defaultIfEmpty(false) // Ensure we always return a Mono
                .doOnSuccess(result -> log.debug("Cached product details for key: {}, result: {}", key, result))
                .doOnError(e -> log.error("Error caching product details for key: {}", key, e))
                .subscribe();
    }
}
