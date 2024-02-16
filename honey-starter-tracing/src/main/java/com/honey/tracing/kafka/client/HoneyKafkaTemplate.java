package com.honey.tracing.kafka.client;

import com.honey.tracing.kafka.producer.interceptor.HoneyKafkaProducerInterceptor;
import com.honey.tracing.kafka.producer.interceptor.HoneyKafkaProducerInterceptorChain;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;

public class HoneyKafkaTemplate<K, V> extends KafkaTemplate<K, V> {

    private List<HoneyKafkaProducerInterceptor<K, V>> kafkaProducerInterceptors;

    public HoneyKafkaTemplate(ProducerFactory<K, V> producerFactory,
                              List<HoneyKafkaProducerInterceptor<K, V>> kafkaProducerInterceptors) {
        super(producerFactory);
        this.kafkaProducerInterceptors = kafkaProducerInterceptors;
    }

    public HoneyKafkaTemplate(ProducerFactory<K, V> producerFactory, Map<String, Object> configOverrides) {
        super(producerFactory, configOverrides);
    }

    public HoneyKafkaTemplate(ProducerFactory<K, V> producerFactory, boolean autoFlush) {
        super(producerFactory, autoFlush);
    }

    public HoneyKafkaTemplate(ProducerFactory<K, V> producerFactory, boolean autoFlush, Map<String, Object> configOverrides) {
        super(producerFactory, autoFlush, configOverrides);
    }

    public ListenableFuture<SendResult<K, V>> actuallySend(ProducerRecord<K, V> producerRecord) {
        return super.doSend(producerRecord);
    }

    @NotNull
    @Override
    public ListenableFuture<SendResult<K, V>> doSend(@NotNull ProducerRecord<K, V> producerRecord) {
        HoneyKafkaProducerInterceptorChain<K, V> kafkaProducerInterceptorChain
                = new HoneyKafkaProducerInterceptorChain<>(kafkaProducerInterceptors.iterator(), this);
        return kafkaProducerInterceptorChain.intercept(producerRecord);
    }

}