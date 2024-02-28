package com.honey.tracing.config;

import com.honey.tracing.database.decorator.HoneyDbExecutorTracingDecorator;
import com.honey.tracing.database.decorator.HoneyDbExecutorTracingSpanDecorator;
import com.honey.tracing.database.interceptor.HoneyDbExecutorTracingInterceptor;
import com.honey.tracing.database.postprocessor.SqlSessionFactoryBeanPostProcessor;
import io.opentracing.Tracer;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnClass(org.apache.ibatis.session.Configuration.class)
@AutoConfigureAfter(HoneyTracingConfig.class)
public class HoneyDbTracingConfig {

    @Bean
    public HoneyDbExecutorTracingInterceptor honeyDbExecutorTracingInterceptor(
            Tracer tracer, List<HoneyDbExecutorTracingDecorator> honeyDbExecutorTracingDecorators) {
        honeyDbExecutorTracingDecorators.add(new HoneyDbExecutorTracingSpanDecorator());
        return new HoneyDbExecutorTracingInterceptor(tracer, honeyDbExecutorTracingDecorators);
    }

    @Bean
    @ConditionalOnMissingClass("org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer")
    public BeanPostProcessor sqlSessionFactoryBeanPostProcessor(List<Interceptor> interceptors) {
        return new SqlSessionFactoryBeanPostProcessor(interceptors);
    }

}