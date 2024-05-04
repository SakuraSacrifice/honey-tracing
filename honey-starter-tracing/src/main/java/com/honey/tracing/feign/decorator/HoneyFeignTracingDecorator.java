package com.honey.tracing.feign.decorator;

import feign.Request;
import feign.Response;
import io.opentracing.Span;

public interface HoneyFeignTracingDecorator {

    void onRequest(Request request, Request.Options options, Span span);

    void onResponse(Response response, Request.Options options, Span span);

    void onError(Exception exception, Request request, Span span);

}