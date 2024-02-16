package com.honey.tracing.kafka.consumer.interceptor;

import com.honey.tracing.kafka.carrier.HoneyKafkaCarrier;
import com.honey.tracing.kafka.consumer.decorator.HoneyKafkaTracingConsumerDecorator;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.List;

import static com.honey.tracing.constant.CommonConstants.FIELD_HOST;
import static com.honey.tracing.constant.CommonConstants.HONEY_KAFKA_NAME;

public class HoneyKafkaConsumerTracingInterceptor<K, V> implements HoneyKafkaConsumerInterceptor {

    private final Tracer tracer;
    private final List<HoneyKafkaTracingConsumerDecorator<K, V>> kafkaTracingConsumerDecorators;

    public HoneyKafkaConsumerTracingInterceptor(
            Tracer tracer, List<HoneyKafkaTracingConsumerDecorator<K, V>> kafkaTracingConsumerDecorators) {
        this.tracer = tracer;
        this.kafkaTracingConsumerDecorators = kafkaTracingConsumerDecorators;
    }

    @Override
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        ConsumerRecord consumerRecord = null;
        // 找到Kafka消息
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof ConsumerRecord) {
                consumerRecord = (ConsumerRecord) arg;
            }
        }

        if (consumerRecord == null) {
            // 没有获取到Kafka消息则不处理链路
            return joinPoint.proceed();
        }

        SpanContext extractSpanContext = tracer.extract(Format.Builtin.HTTP_HEADERS,
                new HoneyKafkaCarrier(consumerRecord.headers()));
        Span span = tracer.buildSpan(HONEY_KAFKA_NAME)
                .asChildOf(extractSpanContext)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
                .start();
        span.setTag(FIELD_HOST, span.getBaggageItem(FIELD_HOST));

        for (HoneyKafkaTracingConsumerDecorator<K, V> kafkaTracingConsumerDecorator : kafkaTracingConsumerDecorators) {
            try {
                kafkaTracingConsumerDecorator.onReceive(span, consumerRecord);
            } catch (Exception e) {
                // do nothing
            }
        }

        Object result;
        try (Scope scope = tracer.activateSpan(span)) {
            try {
                result = joinPoint.proceed();
            } catch (Exception e1) {
                for (HoneyKafkaTracingConsumerDecorator<K, V> kafkaTracingConsumerDecorator : kafkaTracingConsumerDecorators) {
                    try {
                        kafkaTracingConsumerDecorator.onError(span, consumerRecord);
                    } catch (Exception e2) {
                        // do nothing
                    }
                }
                throw e1;
            }

            for (HoneyKafkaTracingConsumerDecorator<K, V> kafkaTracingConsumerDecorator : kafkaTracingConsumerDecorators) {
                try {
                    kafkaTracingConsumerDecorator.onFinished(span, consumerRecord);
                } catch (Exception e) {
                    // do nothing
                }
            }
        } finally {
            span.finish();
        }

        return result;
    }

}