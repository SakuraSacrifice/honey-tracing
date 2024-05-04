package com.honey.tracing.feign.client;

import com.honey.tracing.feign.interceptor.HoneyFeignInterceptor;
import com.honey.tracing.feign.interceptor.HoneyFeignInterceptorChain;
import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;
import java.util.List;

/**
 * 链路日志feign客户端。
 */
public class HoneyFeignClient implements Client {

    /**
     * 底层执行HTTP请求的feign客户端。
     */
    private final Client client;

    private final List<HoneyFeignInterceptor> interceptors;

    public HoneyFeignClient(Client client, List<HoneyFeignInterceptor> interceptors) {
        this.client = client;
        this.interceptors = interceptors;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        HoneyFeignInterceptorChain honeyFeignInterceptorChain = new HoneyFeignInterceptorChain(interceptors.iterator(), client);
        return honeyFeignInterceptorChain.intercept(request, options);
    }

}