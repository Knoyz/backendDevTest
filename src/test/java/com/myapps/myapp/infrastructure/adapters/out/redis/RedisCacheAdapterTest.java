package com.myapps.myapp.infrastructure.adapters.out.redis;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import com.myapps.myapp.domain.model.ProductDetails;

import io.lettuce.core.RedisCommandExecutionException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class RedisCacheAdapterTest {

        @Mock
        private ReactiveListOperations<String, List<String>> listOps;

        @Mock
        private ReactiveValueOperations<String, ProductDetails> valueOps;

        @Mock
        private ReactiveRedisTemplate<String, List<String>> stringListRedisTemplate;

        @Mock
        private ReactiveRedisTemplate<String, ProductDetails> productDetailsRedisTemplate;

        @InjectMocks
        private RedisCacheAdapter adapter;

        private static final String SIMILAR_IDS_PREFIX = "similar-product-ids-of:";
        private static final String DETAILS_PREFIX = "product-details:";

        @BeforeEach
        void setup() {
                adapter = new RedisCacheAdapter(
                                stringListRedisTemplate, productDetailsRedisTemplate);
        }

        @Test
        void shouldReturnFluxWhenCacheHit() {
                String productId = "1";
                String key = SIMILAR_IDS_PREFIX + productId;
                List<String> cachedIds = List.of("A", "B");

                when(stringListRedisTemplate.opsForList()).thenReturn(listOps);

                // range devuelve elementos individuales, no una lista completa
                when(listOps.range(key, 0L, -1L))
                                .thenReturn(Flux.just(cachedIds));

                StepVerifier.create(adapter.getCachedSimilarProducts(productId))
                                .expectNext("A", "B")
                                .verifyComplete();

                verify(listOps).range(key, 0L, -1L);
        }

        @Test
        void shouldReturnEmptyFluxWhenCacheMissEmptyList() {
                String productId = "1";
                String key = SIMILAR_IDS_PREFIX + productId;

                when(stringListRedisTemplate.opsForList()).thenReturn(listOps);

                when(listOps.range(key, 0L, -1L))
                                .thenReturn(Flux.empty());

                StepVerifier.create(adapter.getCachedSimilarProducts(productId))
                                .verifyComplete();

                // Verifica que realmente se llamó con esos argumentos
                verify(listOps).range(key, 0L, -1L);
        }

        @Test
        void shouldHandleRedisCommandExecutionException() {
                String productId = "1";
                String key = SIMILAR_IDS_PREFIX + productId;

                when(stringListRedisTemplate.opsForList()).thenReturn(listOps);

                when(listOps.range(key, 0L, -1L))
                                .thenReturn(Flux.error(new RedisCommandExecutionException("Redis error")));

                StepVerifier.create(adapter.getCachedSimilarProducts(productId))
                                .verifyError(RedisCommandExecutionException.class);

                // Verifica que realmente se llamó con esos argumentos
                verify(listOps).range(key, 0L, -1L);
        }

        @Test
        void shouldHandleGenericExceptionGracefully() {
                String productId = "1";
                String key = SIMILAR_IDS_PREFIX + productId;

                when(stringListRedisTemplate.opsForList()).thenReturn(listOps);

                when(listOps.range(key, 0L, -1L))
                                .thenReturn(Flux.error(new RuntimeException("Key cannot be null")));

                StepVerifier.create(adapter.getCachedSimilarProducts(productId))
                                .verifyError(RuntimeException.class);

                verify(listOps).range(key, 0L, -1L);
        }

        @Test
        void shouldCacheSimilarProductsAndSetTTL() {
                String productId = "123";
                String key = SIMILAR_IDS_PREFIX + productId;
                Flux<String> similarIds = Flux.just("A", "B", "C");
                Duration ttl = Duration.ofHours(1);

                when(stringListRedisTemplate.opsForList()).thenReturn(listOps);

                when(listOps.rightPush(eq(key), anyList()))
                                .thenReturn(Mono.just(1L));
                when(stringListRedisTemplate.expire(key, ttl))
                                .thenReturn(Mono.just(true));

                adapter.cacheSimilarProducts(productId, similarIds, ttl);

                verify(listOps, times(3)).rightPush(eq(key), anyList());
                verify(stringListRedisTemplate).expire(key, ttl);
        }

        @Test
        void shouldNotCacheWhenSimilarIdsIsNull() {
                String productId = "123";
                adapter.cacheSimilarProducts(productId, null, Duration.ofMinutes(10));
                verify(stringListRedisTemplate, never()).opsForList();
                verify(stringListRedisTemplate, never()).expire(anyString(), any());
        }

        @Test
        void shouldHandleErrorOnRightPushGracefully() {
                String productId = "123";
                String key = SIMILAR_IDS_PREFIX + productId;
                Flux<String> similarIds = Flux.just("A");

                when(stringListRedisTemplate.opsForList()).thenReturn(listOps);
                when(listOps.rightPush(eq(key), anyList()))
                                .thenReturn(Mono.error(new RuntimeException("Redis error")));
                when(stringListRedisTemplate.expire(eq(key), any()))
                                .thenReturn(Mono.just(true));

                adapter.cacheSimilarProducts(productId, similarIds, Duration.ofMinutes(5));

                verify(listOps).rightPush(eq(key), anyList());
                verify(stringListRedisTemplate).expire(eq(key), any());
        }

        @Test
        void shouldHandleErrorOnExpireGracefully() {
                String productId = "123";
                String key = SIMILAR_IDS_PREFIX + productId;
                Flux<String> similarIds = Flux.just("A");

                when(stringListRedisTemplate.opsForList()).thenReturn(listOps);
                when(listOps.rightPush(eq(key), anyList()))
                                .thenReturn(Mono.just(1L));
                when(stringListRedisTemplate.expire(eq(key), any()))
                                .thenReturn(Mono.error(new RuntimeException("Expire error")));

                adapter.cacheSimilarProducts(productId, similarIds, Duration.ofMinutes(5));

                verify(listOps).rightPush(eq(key), anyList());
                verify(stringListRedisTemplate).expire(eq(key), any());
        }

        @Test
        void shouldReturnProductDetailsWhenCacheHit() {
                String productId = "42";
                String key = DETAILS_PREFIX + productId;
                ProductDetails details = new ProductDetails(productId, "Product", BigDecimal.valueOf(19.99), true);

                when(productDetailsRedisTemplate.opsForValue()).thenReturn(valueOps);
                when(valueOps.get(key)).thenReturn(Mono.just(details));

                StepVerifier.create(adapter.getCachedProductDetails(productId))
                                .expectNext(details)
                                .verifyComplete();

                verify(productDetailsRedisTemplate).opsForValue();
                verify(valueOps).get(key);
        }

        @Test
        @SuppressWarnings("all")
        void shouldReturnEmptyWhenCacheMissNullId() {
                String productId = "42";
                String key = DETAILS_PREFIX + productId;

                when(productDetailsRedisTemplate.opsForValue()).thenReturn(valueOps);

                RedisCacheAdapter adapter = new RedisCacheAdapter(
                                stringListRedisTemplate, productDetailsRedisTemplate);
                when(valueOps.get(key)).thenReturn(Mono.empty());

                StepVerifier.create(adapter.getCachedProductDetails(productId))
                                .verifyComplete();

                verify(productDetailsRedisTemplate).opsForValue();
                verify(valueOps).get(key);
        }

        @Test
        @SuppressWarnings("all")
        void shouldReturnEmptyOnRedisCommandExecutionException() {
                String productId = "42";
                String key = DETAILS_PREFIX + productId;

                when(productDetailsRedisTemplate.opsForValue()).thenReturn(valueOps);
                RedisCacheAdapter adapter = new RedisCacheAdapter(
                                stringListRedisTemplate, productDetailsRedisTemplate);

                when(valueOps.get(key)).thenReturn(Mono.error(new RedisCommandExecutionException("Redis error")));

                StepVerifier.create(adapter.getCachedProductDetails(productId))
                                .verifyComplete();

                verify(productDetailsRedisTemplate).opsForValue();
                verify(valueOps).get(key);
        }

        @Test
        @SuppressWarnings("all")
        void shouldReturnEmptyOnGenericException() {
                String productId = "42";
                String key = DETAILS_PREFIX + productId;

                when(productDetailsRedisTemplate.opsForValue()).thenReturn(valueOps);
                RedisCacheAdapter adapter = new RedisCacheAdapter(
                                stringListRedisTemplate, productDetailsRedisTemplate);

                when(valueOps.get(key)).thenReturn(Mono.error(new RuntimeException("Some error")));

                StepVerifier.create(adapter.getCachedProductDetails(productId))
                                .verifyComplete();

                verify(productDetailsRedisTemplate).opsForValue();
                verify(valueOps).get(key);
        }

        @Test
        @SuppressWarnings("all")
        void shouldCacheProductDetailsAndSetTTL() {
                String productId = "99";
                String key = DETAILS_PREFIX + productId;
                ProductDetails details = new ProductDetails(productId, "Test", BigDecimal.TEN, true);
                Mono<ProductDetails> detailsMono = Mono.just(details);
                Duration ttl = Duration.ofMinutes(30);

                when(productDetailsRedisTemplate.opsForValue()).thenReturn(valueOps);
                when(valueOps.set(key, details, ttl)).thenReturn(Mono.just(true));

                adapter.cacheProductDetails(productId, detailsMono, ttl);

                verify(productDetailsRedisTemplate).opsForValue();
                verify(valueOps).set(eq(key), eq(details), eq(ttl));
        }

        @Test
        void shouldUseDefaultTTLWhenTTLIsNull() {
                String productId = "100";
                String key = DETAILS_PREFIX + productId;
                ProductDetails details = new ProductDetails(productId, "DefaultTTL", BigDecimal.ONE, false);
                Mono<ProductDetails> detailsMono = Mono.just(details);

                when(productDetailsRedisTemplate.opsForValue()).thenReturn(valueOps);
                when(valueOps.set(eq(key), eq(details), any())).thenReturn(Mono.just(true));

                adapter.cacheProductDetails(productId, detailsMono, null);

                verify(productDetailsRedisTemplate).opsForValue();
                verify(valueOps).set(eq(key), eq(details), any());
        }

        @Test
        void shouldNotCacheWhenProductDetailsIsNull() {
                String productId = "101";
                Mono<ProductDetails> detailsMono = Mono.justOrEmpty(null);

                adapter.cacheProductDetails(productId, detailsMono, Duration.ofMinutes(10));

                verify(productDetailsRedisTemplate, never()).opsForValue();
                verify(valueOps, never()).set(anyString(), any(), any());
        }

        @Test
        void shouldHandleErrorOnSetGracefully() {
                String productId = "102";
                String key = DETAILS_PREFIX + productId;
                ProductDetails details = new ProductDetails(productId, "Error", BigDecimal.ZERO, false);
                Mono<ProductDetails> detailsMono = Mono.just(details);

                when(productDetailsRedisTemplate.opsForValue()).thenReturn(valueOps);
                when(valueOps.set(eq(key), eq(details), any()))
                                .thenReturn(Mono.error(new RuntimeException("Redis error")));

                adapter.cacheProductDetails(productId, detailsMono, Duration.ofMinutes(5));

                verify(productDetailsRedisTemplate).opsForValue();
                verify(valueOps).set(eq(key), eq(details), any());
        }

        @Test
        void shouldNotCacheWhenMonoIsEmpty() {
                String productId = "103";
                Mono<ProductDetails> detailsMono = Mono.empty();

                adapter.cacheProductDetails(productId, detailsMono, Duration.ofMinutes(5));

                verify(productDetailsRedisTemplate, never()).opsForValue();
                verify(valueOps, never()).set(anyString(), any(), any());
        }

}
