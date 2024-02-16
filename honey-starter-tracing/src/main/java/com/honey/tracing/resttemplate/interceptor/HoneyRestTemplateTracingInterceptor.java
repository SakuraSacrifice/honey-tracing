package com.honey.tracing.resttemplate.interceptor;

import com.honey.tracing.util.RequestStackUtil;
import io.jaegertracing.internal.JaegerSpan;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.client.HttpHeadersCarrier;
import io.opentracing.contrib.spring.web.client.RestTemplateSpanDecorator;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

import static com.honey.tracing.constant.CommonConstants.HONEY_REST_TEMPLATE_NAME;

/**
 * RestTemplate客户端的分布式链路追踪拦截器。
 */
public class HoneyRestTemplateTracingInterceptor implements ClientHttpRequestInterceptor {

    private final Tracer tracer;
    private final List<RestTemplateSpanDecorator> restTemplateSpanDecorators;

    public HoneyRestTemplateTracingInterceptor(Tracer tracer, List<RestTemplateSpanDecorator> restTemplateSpanDecorators) {
        this.tracer = tracer;
        this.restTemplateSpanDecorators = restTemplateSpanDecorators;
    }

    @NotNull
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body,
                                        @NotNull ClientHttpRequestExecution execution) throws IOException {
        JaegerSpan parentSpan = (JaegerSpan) tracer.activeSpan();
        if (shouldIgnore(parentSpan)) {
            return execution.execute(request, body);
        }

        ClientHttpResponse clientHttpResponse;
        // 创建代表下游的Span并启动
        Span span = tracer.buildSpan(HONEY_REST_TEMPLATE_NAME)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .start();

        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(request.getHeaders()));

        for (RestTemplateSpanDecorator restTemplateSpanDecorator : restTemplateSpanDecorators) {
            try {
                restTemplateSpanDecorator.onRequest(request, span);
            } catch (Exception e) {
                // do nothing
            }
        }

        // 激活代表下游的Span
        try (Scope scope = tracer.activateSpan(span)) {
            try {
                clientHttpResponse = execution.execute(request, body);
            } catch (Exception e) {
                for (RestTemplateSpanDecorator restTemplateSpanDecorator : restTemplateSpanDecorators) {
                    try {
                        restTemplateSpanDecorator.onError(request, e, span);
                    } catch (Exception onErrorEx) {
                        // do nothing
                    }
                }
                throw e;
            }

            for (RestTemplateSpanDecorator restTemplateSpanDecorator : restTemplateSpanDecorators) {
                try {
                    restTemplateSpanDecorator.onResponse(request, clientHttpResponse, span);
                } catch (Exception e) {
                    // do nothing
                }
            }
        } finally {
            span.finish();
            // 将代表下游的Span作为requestStack记录在parentSpan中
            tracer.activeSpan().log(RequestStackUtil.assembleRequestStack((JaegerSpan) span));
        }

        return clientHttpResponse;
    }

    private boolean shouldIgnore(JaegerSpan activeSpan) {
        return activeSpan == null;
    }

}