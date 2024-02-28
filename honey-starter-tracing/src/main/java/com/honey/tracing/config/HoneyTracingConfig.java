package com.honey.tracing.config;

import com.honey.tracing.async.HoneyTtlScopeManager;
import com.honey.tracing.properties.HoneyTracingProperties;
import com.honey.tracing.reporter.HoneySpanReporter;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.ProbabilisticSampler;
import io.jaegertracing.spi.Reporter;
import io.jaegertracing.spi.Sampler;
import io.opentracing.ScopeManager;
import io.opentracing.Tracer;
import io.opentracing.util.ThreadLocalScopeManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.honey.tracing.constant.CommonConstants.DEFAULT_SAMPLE_RATE;
import static com.honey.tracing.constant.CommonConstants.HONEY_TRACER_NAME;

/**
 * 分布式链路追踪配置类。
 */
@Configuration
@EnableConfigurationProperties({HoneyTracingProperties.class})
@ConditionalOnProperty(prefix = "honey.tracing", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HoneyTracingConfig {

    @Bean
    @ConditionalOnMissingBean(Sampler.class)
    public Sampler sampler() {
        return new ProbabilisticSampler(DEFAULT_SAMPLE_RATE);
    }

    @Bean
    @ConditionalOnMissingBean(Reporter.class)
    public Reporter reporter() {
        return new HoneySpanReporter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "honey.tracing.async", name = "enabled", havingValue = "true")
    public ScopeManager honeyTtlScopeManager() {
        return new HoneyTtlScopeManager();
    }

    @Bean
    @ConditionalOnProperty(prefix = "honey.tracing.async", name = "enabled", havingValue = "false", matchIfMissing = true)
    public ScopeManager threadLocalScopeManager() {
        return new ThreadLocalScopeManager();
    }

    @Bean
    @ConditionalOnMissingBean(Tracer.class)
    public Tracer tracer(Sampler sampler, Reporter reporter, ScopeManager scopeManager) {
        return new JaegerTracer.Builder(HONEY_TRACER_NAME)
                .withTraceId128Bit()
                .withZipkinSharedRpcSpan()
                .withSampler(sampler)
                .withReporter(reporter)
                .withScopeManager(scopeManager)
                .build();
    }

}