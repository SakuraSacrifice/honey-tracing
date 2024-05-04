package com.honey.tracing.feign.interceptor;

import com.honey.tracing.feign.client.HoneyFeignClient;
import com.honey.tracing.feign.decorator.HoneyFeignTracingDecorator;
import com.honey.tracing.util.RequestStackUtil;
import feign.Request;
import feign.Response;
import io.jaegertracing.internal.JaegerSpan;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.honey.tracing.constant.CommonConstants.HONEY_FEIGN_NAME;

/**
 * 适用于{@link HoneyFeignClient}的链路追踪拦截器。
 */
public class HoneyFeignTracingInterceptor implements HoneyFeignInterceptor {

    private final Tracer tracer;

    private final List<HoneyFeignTracingDecorator> honeyFeignTracingDecorators;

    public HoneyFeignTracingInterceptor(Tracer tracer, List<HoneyFeignTracingDecorator> honeyFeignTracingDecorators) {
        this.tracer = tracer;
        this.honeyFeignTracingDecorators = honeyFeignTracingDecorators;
    }

    @Override
    public Response intercept(Request request, Request.Options options, HoneyFeignInterceptorChain interceptorChain) throws IOException {
        if (tracer.activeSpan() == null) {
            return interceptorChain.intercept(request, options);
        }

        Span span = tracer.buildSpan(HONEY_FEIGN_NAME)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .start();

        request = inject(span, request);

        for (HoneyFeignTracingDecorator honeyFeignTracingDecorator : honeyFeignTracingDecorators) {
            try {
                honeyFeignTracingDecorator.onRequest(request, options, span);
            } catch (Exception e) {
                // do nothing
            }
        }

        Response response;
        try (Scope scope = tracer.activateSpan(span)) {
            try {
                response = interceptorChain.intercept(request, options);
            } catch (Exception e) {
                for (HoneyFeignTracingDecorator honeyFeignTracingDecorator : honeyFeignTracingDecorators) {
                    try {
                        honeyFeignTracingDecorator.onError(e, request, span);
                    } catch (Exception onErrorEx) {
                        // do nothing
                    }
                }
                throw e;
            }

            for (HoneyFeignTracingDecorator honeyFeignTracingDecorator : honeyFeignTracingDecorators) {
                try {
                    honeyFeignTracingDecorator.onResponse(response, options, span);
                } catch (Exception onErrorEx) {
                    // do nothing
                }
            }
        } finally {
            span.finish();
            // 将代表下游的Span作为requestStack记录在parentSpan中
            tracer.activeSpan().log(RequestStackUtil.assembleRequestStack((JaegerSpan) span));
        }

        return response;
    }

    private Request inject(Span span, Request request) {
        Map<String, Collection<String>> headers = new HashMap<>(request.headers());
        // 将链路信息注入到headers中
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS,
                new HttpHeadersInjectCarrier(headers));
        // 基于headers重新创建Request
        return Request.create(request.httpMethod(), request.url(), headers,
                request.body(), request.charset(), request.requestTemplate());
    }

}