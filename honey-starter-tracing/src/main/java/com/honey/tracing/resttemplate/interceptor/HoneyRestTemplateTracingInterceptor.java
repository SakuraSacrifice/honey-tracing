package com.honey.tracing.resttemplate.interceptor;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.client.HttpHeadersCarrier;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static com.honey.tracing.constant.CommonConstants.HONEY_REST_TEMPLATE_NAME;

/**
 * RestTemplate客户端的分布式链路追踪拦截器。
 */
public class HoneyRestTemplateTracingInterceptor implements ClientHttpRequestInterceptor {

    private final Tracer tracer;

    public HoneyRestTemplateTracingInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @NotNull
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        Span span = tracer.buildSpan(HONEY_REST_TEMPLATE_NAME)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .start();
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(request.getHeaders()));

        try (Scope scope = tracer.activateSpan(span)) {
            return execution.execute(request, body);
        } catch (IOException e) {
            Tags.ERROR.set(span, Boolean.TRUE);
            throw e;
        } finally {
            span.finish();
        }
    }

}