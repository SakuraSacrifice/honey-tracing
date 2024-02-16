package com.honey.tracing.kafka.consumer.decorator;

import io.opentracing.Span;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Kafka消费者链路追踪装饰器。
 */
public interface HoneyKafkaTracingConsumerDecorator<K, V> {

    void onReceive(Span span, ConsumerRecord<K, V> consumerRecord);

    void onFinished(Span span, ConsumerRecord<K, V> consumerRecord);

    void onError(Span span, ConsumerRecord<K, V> consumerRecord);

}