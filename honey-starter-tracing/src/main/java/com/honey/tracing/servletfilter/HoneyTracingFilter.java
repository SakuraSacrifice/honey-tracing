package com.honey.tracing.servletfilter;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.web.servlet.filter.HttpServletRequestExtractAdapter;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HoneyTracingFilter implements Filter {

    private final Tracer tracer;

    public HoneyTracingFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        SpanContext extractedSpanContext = tracer.extract(Format.Builtin.HTTP_HEADERS,
                new HttpServletRequestExtractAdapter(request));

        Span span = tracer.buildSpan(request.getMethod())
                .asChildOf(extractedSpanContext)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
                .start();

        try (Scope scope = tracer.activateSpan(span)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (IOException | ServletException e) {
            Tags.ERROR.set(span, Boolean.TRUE);
            throw e;
        } finally {
            span.finish();
        }
    }

}