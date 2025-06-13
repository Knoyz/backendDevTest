package com.myapps.myapp.infrastructure.adapters.out.redis;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import com.myapps.myapp.domain.model.ProductDetails;

import io.lettuce.core.RedisCommandExecutionException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class RedisCacheAdapterTest {

    private ReactiveListOperations<String, List<String>> listOps;
    @Mock
    private ReactiveRedisTemplate<String, List<String>> stringListRedisTemplate;

    @Mock
    private ReactiveRedisTemplate<String, ProductDetails> productDetailsRedisTemplate;
    @InjectMocks
    private RedisCacheAdapter adapter;

    private static final String SIMILAR_IDS_PREFIX = "similar:";

    @BeforeEach
    void setUp() {
        stringListRedisTemplate = mock(ReactiveRedisTemplate.class);
        listOps = mock(ReactiveListOperations.class);
        when(stringListRedisTemplate.opsForList()).thenReturn(listOps);

        adapter = new RedisCacheAdapter(stringListRedisTemplate, productDetailsRedisTemplate); // asegúrate que uses el
                                                                                               // constructor correcto
    }

    @Test
    void shouldReturnFluxWhenCacheHit() {
        String productId = "1";
        String key = "similar-product-ids-of:" + productId;
        List<String> cachedIds = List.of("A", "B");

        when(listOps.range(key, 0L, -1L))
                .thenReturn(Flux.just(cachedIds));

        StepVerifier.create(adapter.getCachedSimilarProducts(productId))
                .expectNext("A", "B")
                .verifyComplete();

        // Verifica que realmente se llamó con esos argumentos
        verify(listOps).range(key, 0L, -1L);
    }

    @Test
    void shouldReturnEmptyFluxWhenCacheMissEmptyList() {
        String productId = "1";
        String key = "similar-product-ids-of:" + productId;

        when(listOps.range(key, 0L, -1L))
                .thenReturn(Flux.empty());

        StepVerifier.create(adapter.getCachedSimilarProducts(productId))
                .verifyComplete();

        // Verifica que realmente se llamó con esos argumentos
        verify(listOps).range(key, 0L, -1L);
    }

    @Test
    void shouldReturnEmptyFluxWhenCacheMissNull() {
        String productId = "1";
        String key = "similar-product-ids-of:" + productId;

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
        String key = "similar-product-ids-of:" + productId;

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
        String key = "similar-product-ids-of:" + productId;

        when(listOps.range(key, 0L, -1L))
                .thenReturn(Flux.error(new RuntimeException("Key cannot be null")));

        StepVerifier.create(adapter.getCachedSimilarProducts(productId))
                .verifyError(RuntimeException.class);

        verify(listOps).range(key, 0L, -1L);
    }
}
