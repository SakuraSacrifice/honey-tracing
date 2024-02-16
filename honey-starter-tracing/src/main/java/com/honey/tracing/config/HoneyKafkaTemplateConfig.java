package com.honey.tracing.config;

import com.honey.tracing.kafka.client.HoneyKafkaTemplate;
import com.honey.tracing.kafka.producer.interceptor.HoneyKafkaProducerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ProducerFactory;

import java.util.List;

@Configuration
@AutoConfigureBefore(KafkaAutoConfiguration.class)
public class HoneyKafkaTemplateConfig {

    @Bean
    public HoneyKafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory,
                                                  List<HoneyKafkaProducerInterceptor<Object, Object>> kafkaProducerInterceptors) {
        return new HoneyKafkaTemplate<>(kafkaProducerFactory, kafkaProducerInterceptors);
    }

}