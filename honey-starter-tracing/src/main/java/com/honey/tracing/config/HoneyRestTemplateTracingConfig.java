package com.honey.tracing.config;

import com.honey.tracing.resttemplate.decorator.HoneyRestTemplateSpanDecorator;
import com.honey.tracing.resttemplate.interceptor.HoneyRestTemplateTracingInterceptor;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.client.RestTemplateSpanDecorator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * RestTemplate分布式链路追踪配置类。
 */
@ConditionalOnBean(RestTemplate.class)
@Configuration
@AutoConfigureAfter(HoneyTracingConfig.class)
@Import(HoneyRestTemplateSpanDecorator.class)
public class HoneyRestTemplateTracingConfig {

    public HoneyRestTemplateTracingConfig(List<RestTemplate> restTemplates, Tracer tracer,
                                          List<RestTemplateSpanDecorator> restTemplateSpanDecorators) {
        for (RestTemplate restTemplate : restTemplates) {
            // todo 还要判断RestTemplate里是否已经添加了HoneyRestTemplateTracingInterceptor
            restTemplate.getInterceptors().add(new HoneyRestTemplateTracingInterceptor(tracer, restTemplateSpanDecorators));
        }
    }

}