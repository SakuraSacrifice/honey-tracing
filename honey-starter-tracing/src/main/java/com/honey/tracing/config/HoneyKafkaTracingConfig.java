package com.honey.tracing.config;

import com.honey.tracing.kafka.consumer.decorator.HoneyKafkaTracingConsumerDecorator;
import com.honey.tracing.kafka.consumer.interceptor.HoneyKafkaConsumerInterceptor;
import com.honey.tracing.kafka.consumer.interceptor.HoneyKafkaConsumerTracingAspect;
import com.honey.tracing.kafka.consumer.interceptor.HoneyKafkaConsumerTracingInterceptor;
import com.honey.tracing.kafka.producer.decorator.HoneyKafkaTracingProducerDecorator;
import com.honey.tracing.kafka.producer.interceptor.HoneyKafkaProducerInterceptor;
import com.honey.tracing.kafka.producer.interceptor.HoneyKafkaProducerTracingInterceptor;
import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@Configuration
@ConditionalOnBean(KafkaTemplate.class)
@AutoConfigureAfter(HoneyTracingConfig.class)
public class HoneyKafkaTracingConfig {

    @Bean
    public HoneyKafkaProducerInterceptor kafkaProducerTracingInterceptor(
            Tracer tracer, List<HoneyKafkaTracingProducerDecorator<Object, Object>> kafkaTracingProducerDecorators) {
        return new HoneyKafkaProducerTracingInterceptor<>(tracer, kafkaTracingProducerDecorators);
    }

    @Bean
    public HoneyKafkaConsumerInterceptor honeyKafkaConsumerInterceptor(
            Tracer tracer, List<HoneyKafkaTracingConsumerDecorator<Object, Object>> kafkaTracingProducerDecorators) {
        return new HoneyKafkaConsumerTracingInterceptor(tracer, kafkaTracingProducerDecorators);
    }

    @Bean
    public HoneyKafkaConsumerTracingAspect honeyKafkaConsumerTracingAspect(
            HoneyKafkaConsumerInterceptor kafkaConsumerTracingInterceptor) {
        return new HoneyKafkaConsumerTracingAspect(kafkaConsumerTracingInterceptor);
    }

}