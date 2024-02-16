package com.honey.tracing.kafka.producer.interceptor;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface HoneyKafkaProducerInterceptor<K, V> {

    /**
     * 拦截Kafka生产者的消息发送。
     *
     * @param producerRecord 生产者发送的消息。
     */
    ListenableFuture<SendResult<K, V>> intercept(
            ProducerRecord<K, V> producerRecord,
            HoneyKafkaProducerInterceptorChain<K, V> kafkaProducerInterceptorChain);

}