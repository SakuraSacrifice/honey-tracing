package com.honey.tracing.kafka.producer.interceptor;

import com.honey.tracing.kafka.client.HoneyKafkaTemplate;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Iterator;

public class HoneyKafkaProducerInterceptorChain<K, V> {

    private final Iterator<HoneyKafkaProducerInterceptor<K, V>> kafkaProducerInterceptors;
    private final HoneyKafkaTemplate<K, V> honeyKafkaTemplate;

    public HoneyKafkaProducerInterceptorChain(Iterator<HoneyKafkaProducerInterceptor<K, V>> kafkaProducerInterceptors,
                                              HoneyKafkaTemplate<K, V> honeyKafkaTemplate) {
        this.kafkaProducerInterceptors = kafkaProducerInterceptors;
        this.honeyKafkaTemplate = honeyKafkaTemplate;
    }

    public HoneyKafkaTemplate<K, V> getHoneyKafkaTemplate() {
        return honeyKafkaTemplate;
    }

    public ListenableFuture<SendResult<K, V>> intercept(ProducerRecord<K, V> producerRecord) {
        if (kafkaProducerInterceptors.hasNext()) {
            // 拦截器没执行完则先执行拦截器
            return kafkaProducerInterceptors.next().intercept(producerRecord, this);
        }
        // 拦截器全部执行完后才发送消息
        return honeyKafkaTemplate.actuallySend(producerRecord);
    }

}