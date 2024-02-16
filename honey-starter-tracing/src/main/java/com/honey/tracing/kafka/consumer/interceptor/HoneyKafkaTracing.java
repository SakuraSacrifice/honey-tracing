package com.honey.tracing.kafka.consumer.interceptor;

import java.lang.annotation.*;

/**
 * Kafka消费者方法使用该注解。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HoneyKafkaTracing {

}