package com.honey.tracing.feign.decorator;

import com.honey.tracing.util.UrlUtil;
import feign.Request;
import feign.Response;
import io.jaegertracing.internal.JaegerSpan;
import io.opentracing.Span;
import org.springframework.http.HttpStatus;

import static com.honey.tracing.constant.CommonConstants.FIELD_HOST;
import static com.honey.tracing.constant.CommonConstants.FIELD_HTTP_CODE;

public class HoneyFeignTracingSpanDecorator implements HoneyFeignTracingDecorator {

    @Override
    public void onRequest(Request request, Request.Options options, Span span) {
        ((JaegerSpan) span).setTag(FIELD_HOST, UrlUtil.getHostFromUri(request.url()));
    }

    @Override
    public void onResponse(Response response, Request.Options options, Span span) {
        try {
            ((JaegerSpan) span).setTag(FIELD_HTTP_CODE, response.status());
        } catch (Exception e) {
            ((JaegerSpan) span).setTag(FIELD_HTTP_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public void onError(Exception exception, Request request, Span span) {
        ((JaegerSpan) span).setTag(FIELD_HTTP_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}