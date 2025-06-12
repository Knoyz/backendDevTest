package com.myapps.myapp.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.myapps.myapp.domain.model.ProductDetails;

@Configuration
public class RedisConfig {

        @Bean
        @Primary
        public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
                return new LettuceConnectionFactory("localhost", 6379);
        }

        @Bean
        public ReactiveRedisTemplate<String, List<String>> stringListRedisTemplate(
                        ReactiveRedisConnectionFactory factory) {
                @SuppressWarnings("unchecked")
                Jackson2JsonRedisSerializer<List<String>> serializer = new Jackson2JsonRedisSerializer<>(
                                (Class<List<String>>) (Class<?>) List.class);
                RedisSerializationContext<String, List<String>> context = RedisSerializationContext
                                .<String, List<String>>newSerializationContext(new StringRedisSerializer())
                                .value(serializer)
                                .build();
                return new ReactiveRedisTemplate<>(factory, context);
        }

        @Bean
        public ReactiveRedisTemplate<String, ProductDetails> productDetailsRedisTemplate(
                        ReactiveRedisConnectionFactory factory) {
                Jackson2JsonRedisSerializer<ProductDetails> serializer = new Jackson2JsonRedisSerializer<>(
                                ProductDetails.class);
                RedisSerializationContext<String, ProductDetails> context = RedisSerializationContext
                                .<String, ProductDetails>newSerializationContext(new StringRedisSerializer())
                                .value(serializer)
                                .build();
                return new ReactiveRedisTemplate<>(factory, context);
        }
}
