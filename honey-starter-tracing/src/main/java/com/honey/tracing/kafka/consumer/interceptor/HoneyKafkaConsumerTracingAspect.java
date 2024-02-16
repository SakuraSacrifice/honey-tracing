package com.honey.tracing.kafka.consumer.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class HoneyKafkaConsumerTracingAspect {

    private final HoneyKafkaConsumerInterceptor kafkaConsumerTracingInterceptor;

    public HoneyKafkaConsumerTracingAspect(HoneyKafkaConsumerInterceptor kafkaConsumerTracingInterceptor) {
        this.kafkaConsumerTracingInterceptor = kafkaConsumerTracingInterceptor;
    }

    @Pointcut("@annotation(com.honey.tracing.kafka.consumer.interceptor.HoneyKafkaTracing)")
    private void kafkaTracing() {

    }

    @Around("kafkaTracing()")
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        return kafkaConsumerTracingInterceptor.intercept(joinPoint);
    }

}