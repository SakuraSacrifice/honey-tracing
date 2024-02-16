package com.honey.tracing.resttemplate.decorator;

import com.honey.tracing.util.UrlUtil;
import io.jaegertracing.internal.JaegerSpan;
import io.opentracing.Span;
import io.opentracing.contrib.spring.web.client.RestTemplateSpanDecorator;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import static com.honey.tracing.constant.CommonConstants.FIELD_HOST;
import static com.honey.tracing.constant.CommonConstants.FIELD_HTTP_CODE;

/**
 * {@link RestTemplate}的{@link Span}装饰器。
 */
public class HoneyRestTemplateSpanDecorator implements RestTemplateSpanDecorator {

    @Override
    public void onRequest(HttpRequest request, Span span) {
        ((JaegerSpan) span).setTag(FIELD_HOST, UrlUtil.getHostFromUri(request.getURI().toString()));
    }

    @Override
    public void onResponse(HttpRequest request, ClientHttpResponse response, Span span) {
        try {
            ((JaegerSpan) span).setTag(FIELD_HTTP_CODE, response.getRawStatusCode());
        } catch (Exception e) {
            ((JaegerSpan) span).setTag(FIELD_HTTP_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public void onError(HttpRequest request, Throwable ex, Span span) {
        // todo 调用下游失败时设置500好像有点不合理
        ((JaegerSpan) span).setTag(FIELD_HTTP_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}