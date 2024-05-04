package com.honey.tracing.config;

import com.honey.tracing.feign.decorator.HoneyFeignTracingDecorator;
import com.honey.tracing.feign.decorator.HoneyFeignTracingSpanDecorator;
import com.honey.tracing.feign.interceptor.HoneyFeignInterceptor;
import com.honey.tracing.feign.interceptor.HoneyFeignTracingInterceptor;
import feign.Client;
import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnClass(Client.class)
@AutoConfigureAfter({HoneyDefaultClientFeignConfig.class, HoneyHttpClientFeignConfig.class})
public class HoneyFeignTracingConfig {

    @Bean
    @ConditionalOnMissingBean(HoneyFeignTracingSpanDecorator.class)
    public HoneyFeignTracingDecorator honeyFeignTracingSpanDecorator() {
        return new HoneyFeignTracingSpanDecorator();
    }

    @Bean
    @ConditionalOnMissingBean(HoneyFeignTracingInterceptor.class)
    public HoneyFeignInterceptor honeyFeignTracingInterceptor(
            Tracer tracer, List<HoneyFeignTracingDecorator> honeyFeignTracingDecorators) {
        return new HoneyFeignTracingInterceptor(tracer, honeyFeignTracingDecorators);
    }

}