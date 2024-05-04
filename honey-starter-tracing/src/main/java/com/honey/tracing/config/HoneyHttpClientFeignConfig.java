package com.honey.tracing.config;

import com.honey.tracing.feign.client.HoneyFeignClient;
import com.honey.tracing.feign.interceptor.HoneyFeignInterceptor;
import feign.Client;
import feign.httpclient.ApacheHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 当容器中有{@link ApacheHttpClient}的bean时该配置类生效。<br/>
 * 用于基于{@link ApacheHttpClient}创建{@link HoneyFeignClient}。
 */
@Configuration
@ConditionalOnBean(ApacheHttpClient.class)
public class HoneyHttpClientFeignConfig {

    @Bean
    public Client feignClient(ApacheHttpClient httpClient, List<HoneyFeignInterceptor> interceptors) {
        return new HoneyFeignClient(httpClient, interceptors);
    }

}