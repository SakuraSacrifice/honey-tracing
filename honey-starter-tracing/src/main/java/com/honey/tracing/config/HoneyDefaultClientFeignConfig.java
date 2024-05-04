package com.honey.tracing.config;

import com.honey.tracing.feign.client.HoneyFeignClient;
import com.honey.tracing.feign.interceptor.HoneyFeignInterceptor;
import feign.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 当容器中没有{@link Client}的bean时该配置类生效。<br/>
 * 用于基于{@link Client.Default}创建{@link HoneyFeignClient}。
 */
@Configuration
@ConditionalOnMissingBean(Client.class)
public class HoneyDefaultClientFeignConfig {

    @Bean
    public Client feignClient(List<HoneyFeignInterceptor> interceptors) {
        return new HoneyFeignClient(new Client.Default(null, null), interceptors);
    }

}