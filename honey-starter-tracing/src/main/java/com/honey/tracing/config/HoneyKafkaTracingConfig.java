package com.honey.tracing.config;

import com.honey.tracing.kafka.producer.decorator.HoneyKafkaTracingProducerDecorator;
import com.honey.tracing.kafka.producer.interceptor.HoneyKafkaProducerInterceptor;
import com.honey.tracing.kafka.producer.interceptor.HoneyKafkaProducerTracingInterceptor;
import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AutoConfigureAfter(HoneyTracingConfig.class)
public class HoneyKafkaTracingConfig {

    @Bean
    public HoneyKafkaProducerInterceptor kafkaProducerTracingInterceptor(
            Tracer tracer, List<HoneyKafkaTracingProducerDecorator<Object, Object>> kafkaTracingProducerDecorators) {
        return new HoneyKafkaProducerTracingInterceptor<>(tracer, kafkaTracingProducerDecorators);
    }

}