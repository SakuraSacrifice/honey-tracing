package com.honey.tracing.config;

import com.honey.tracing.servletfilter.HoneyTracingFilter;
import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.honey.tracing.constant.CommonConstants.ALL_URL_PATTERN_STR;

/**
 * Servlet过滤器配置类。
 */
@Configuration
@AutoConfigureAfter(HoneyTracingConfig.class)
public class HoneyTracingFilterConfig {

    @Bean
    public FilterRegistrationBean<HoneyTracingFilter> honeyTracingFilter(Tracer tracer) {
        HoneyTracingFilter honeyTracingFilter = new HoneyTracingFilter(tracer);
        FilterRegistrationBean<HoneyTracingFilter> filterFilterRegistrationBean
                = new FilterRegistrationBean<>(honeyTracingFilter);
        filterFilterRegistrationBean.addUrlPatterns(ALL_URL_PATTERN_STR);
        return filterFilterRegistrationBean;
    }

}