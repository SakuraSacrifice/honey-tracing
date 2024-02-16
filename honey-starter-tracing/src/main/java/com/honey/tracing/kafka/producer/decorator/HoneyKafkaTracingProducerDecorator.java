package com.honey.tracing.kafka.producer.decorator;

import io.opentracing.Span;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Kafka生产者链路追踪装饰器。
 */
public interface HoneyKafkaTracingProducerDecorator<K, V> {

    void onSend(Span span, ProducerRecord<K, V> producerRecord);

    void onSuccess(Span span, ProducerRecord<K, V> producerRecord);

    void onError(Span span, ProducerRecord<K, V> producerRecord);

}