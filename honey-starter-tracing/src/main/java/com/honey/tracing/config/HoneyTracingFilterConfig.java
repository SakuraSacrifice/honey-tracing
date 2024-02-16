package com.honey.tracing.config;

import com.honey.tracing.properties.HoneyTracingProperties;
import com.honey.tracing.servletfilter.decorator.HoneyServletFilterSpanDecorator;
import io.opentracing.Tracer;
import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import io.opentracing.contrib.web.servlet.filter.TracingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Servlet过滤器配置类。
 */
@Configuration
@AutoConfigureAfter(HoneyTracingConfig.class)
public class HoneyTracingFilterConfig {

    @Autowired
    private HoneyTracingProperties honeyTracingProperties;

    @Bean
    public FilterRegistrationBean<TracingFilter> honeyTracingFilter(Tracer tracer,
                                                                    List<ServletFilterSpanDecorator> decorators) {
        String urlPattern = honeyTracingProperties.getHttpUrl().getUrlPattern();
        String skipPatternStr = honeyTracingProperties.getHttpUrl().getSkipPattern();
        Pattern skipPattern = Pattern.compile(skipPatternStr);

        FilterRegistrationBean<TracingFilter> registrationBean = new FilterRegistrationBean<>();
        // 为TracingFilter添加注册时的urlPattern
        registrationBean.addUrlPatterns(urlPattern);
        // TracingFilter的优先级要尽可能的高
        registrationBean.setOrder(Integer.MIN_VALUE);
        // 创建TracingFilter时指定装饰器集合和skipPattern
        // 这里暂时传入空的装饰器集合
        registrationBean.setFilter(new TracingFilter(tracer, decorators, skipPattern));

        return registrationBean;
    }

    @Bean
    public ServletFilterSpanDecorator honeyServletFilterSpanDecorator() {
        return new HoneyServletFilterSpanDecorator();
    }

}