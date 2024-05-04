package com.honey.tracing.feign.interceptor;

import com.honey.tracing.feign.client.HoneyFeignClient;
import feign.Request;
import feign.Response;

import java.io.IOException;

/**
 * 适用于{@link HoneyFeignClient}的拦截器。
 */
public interface HoneyFeignInterceptor {

    Response intercept(Request request, Request.Options options, HoneyFeignInterceptorChain interceptorChain) throws IOException;

}