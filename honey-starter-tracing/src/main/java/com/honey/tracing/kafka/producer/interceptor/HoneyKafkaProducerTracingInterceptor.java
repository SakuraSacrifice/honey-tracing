package com.honey.tracing.kafka.producer.interceptor;

import com.honey.tracing.kafka.carrier.HoneyKafkaCarrier;
import com.honey.tracing.kafka.producer.decorator.HoneyKafkaTracingProducerDecorator;
import com.honey.tracing.util.RequestStackUtil;
import io.jaegertracing.internal.JaegerSpan;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

import static com.honey.tracing.constant.CommonConstants.FIELD_HOST;
import static com.honey.tracing.constant.CommonConstants.HONEY_KAFKA_NAME;
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

public class HoneyKafkaProducerTracingInterceptor<K, V> implements HoneyKafkaProducerInterceptor<K, V> {

    private final Tracer tracer;
    private final List<HoneyKafkaTracingProducerDecorator<K, V>> kafkaTracingProducerDecorators;

    public HoneyKafkaProducerTracingInterceptor(Tracer tracer, List<HoneyKafkaTracingProducerDecorator<K, V>> kafkaTracingProducerDecorators) {
        this.tracer = tracer;
        this.kafkaTracingProducerDecorators = kafkaTracingProducerDecorators;
    }

    @Override
    public ListenableFuture<SendResult<K, V>> intercept(ProducerRecord<K, V> producerRecord,
                                                        HoneyKafkaProducerInterceptorChain<K, V> kafkaProducerInterceptorChain) {
        if (tracer.activeSpan() == null) {
            return kafkaProducerInterceptorChain.intercept(producerRecord);
        }

        // 生成Kafka生产者对应的Span
        // 类似于RestTemplate调用前的Span
        Span span = tracer.buildSpan(HONEY_KAFKA_NAME)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .start();

        for (HoneyKafkaTracingProducerDecorator<K, V> kafkaTracingProducerDecorator : kafkaTracingProducerDecorators) {
            try {
                kafkaTracingProducerDecorator.onSend(span, producerRecord);
            } catch (Exception e) {
                // do nothing
            }
        }

        ListenableFuture<SendResult<K, V>> result;
        try (Scope scope = tracer.activateSpan(span)) {
            // 设置Kafka服务端的host
            String hostString = kafkaProducerInterceptorChain.getHoneyKafkaTemplate().getProducerFactory()
                    .getConfigurationProperties().get(BOOTSTRAP_SERVERS_CONFIG).toString();
            span.setTag(FIELD_HOST, hostString.substring(1, hostString.length() - 1));
            // host需要传递给消费者
            span.setBaggageItem(FIELD_HOST, hostString.substring(1, hostString.length() - 1));

            // 把SpanContext注入到ProducerRecord的Headers中
            tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HoneyKafkaCarrier(producerRecord.headers()));

            try {
                result = kafkaProducerInterceptorChain.intercept(producerRecord);
            } catch (Exception e1) {
                for (HoneyKafkaTracingProducerDecorator<K, V> kafkaTracingProducerDecorator : kafkaTracingProducerDecorators) {
                    try {
                        kafkaTracingProducerDecorator.onError(span, producerRecord);
                    } catch (Exception e2) {
                        // do nothing
                    }
                }
                throw e1;
            }

            for (HoneyKafkaTracingProducerDecorator<K, V> kafkaTracingProducerDecorator : kafkaTracingProducerDecorators) {
                try {
                    kafkaTracingProducerDecorator.onSuccess(span, producerRecord);
                } catch (Exception e) {
                    // do nothing
                }
            }
        } finally {
            span.finish();
            tracer.activeSpan().log(RequestStackUtil.assembleRequestStack((JaegerSpan) span));
        }

        return result;
    }

}