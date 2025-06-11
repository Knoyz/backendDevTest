package com.backendDevTest.myApp.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.backendDevTest.myApp.domain.events.ProductsSimilarIdsEvent;
import com.backendDevTest.myApp.domain.events.RequestDataWithProductIdEvent;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, RequestDataWithProductIdEvent> producerFactoryRequestDataWithProductIdEvent() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "Localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "Localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic similarProductIdsTopic() {
        return new NewTopic("similar-product-ids", 1, (short) 1);
    }

    @Bean
    public NewTopic productDetailsTopic() {
        return new NewTopic("product-details", 1, (short) 1);
    }

    @Bean
    public ConsumerFactory<String, ProductsSimilarIdsEvent> ConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ProductsSimilarIdsEvent>> ProductsSimilarIdsEventConsumer() {
        ConcurrentKafkaListenerContainerFactory<String, ProductsSimilarIdsEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ConsumerFactory());
        return factory;
    }

    // @Bean
    // public ConsumerFactory<String, ResponseProductDetailsEvent>
    // ResponseProductDetailsEventconsumerFactory() {
    // Map<String, Object> configProps = new HashMap<>();
    // configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    // configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
    // StringSerializer.class);
    // configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    // JsonSerializer.class);
    // return new DefaultKafkaConsumerFactory<>(configProps);
    // }

    // @Bean
    // public
    // KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,
    // ResponseProductDetailsEvent>> ResponseProductDetailsEventconsumer() {
    // ConcurrentKafkaListenerContainerFactory<String, ResponseProductDetailsEvent>
    // factory = new ConcurrentKafkaListenerContainerFactory<>();
    // factory.setConsumerFactory(ResponseProductDetailsEventconsumerFactory());
    // return factory;
    // }

    // @Bean
    // public ConsumerFactory<String, ProductsSimilarIdsEvent>
    // ProductsSimilarIdsEventConsumerFactory() {
    // Map<String, Object> configProps = new HashMap<>();
    // configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    // configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
    // StringSerializer.class);
    // configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    // JsonSerializer.class);
    // return new DefaultKafkaConsumerFactory<>(configProps);
    // }

    // @Bean
    // public
    // KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,
    // ProductsSimilarIdsEvent>> ProductsSimilarIdsEventConsumer() {
    // ConcurrentKafkaListenerContainerFactory<String, ProductsSimilarIdsEvent>
    // factory = new ConcurrentKafkaListenerContainerFactory<>();
    // factory.setConsumerFactory(ProductsSimilarIdsEventConsumerFactory());
    // return factory;
    // }
}
