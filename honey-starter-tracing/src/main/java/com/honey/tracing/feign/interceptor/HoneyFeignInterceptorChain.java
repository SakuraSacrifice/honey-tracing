package com.honey.tracing.feign.interceptor;

import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;
import java.util.Iterator;

public class HoneyFeignInterceptorChain {

    private final Iterator<HoneyFeignInterceptor> interceptors;

    private final Client client;

    public HoneyFeignInterceptorChain(Iterator<HoneyFeignInterceptor> interceptors, Client client) {
        this.interceptors = interceptors;
        this.client = client;
    }

    public Response intercept(Request request, Request.Options options) throws IOException {
        if (interceptors.hasNext()) {
            return interceptors.next().intercept(request, options, this);
        }
        return client.execute(request, options);
    }

}