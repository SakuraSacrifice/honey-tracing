package com.honey.tracing.kafka.consumer.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

public interface HoneyKafkaConsumerInterceptor {

    Object intercept(ProceedingJoinPoint joinPoint) throws Throwable;

}