package com.honey.tracing.servletfilter.decorator;

import io.opentracing.Span;
import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import io.opentracing.contrib.web.servlet.filter.TracingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.honey.tracing.constant.CommonConstants.*;

/**
 * {@link TracingFilter}的装饰器。
 */
public class HoneyServletFilterSpanDecorator implements ServletFilterSpanDecorator {

    @Override
    public void onRequest(HttpServletRequest httpServletRequest, Span span) {
        span.setTag(FIELD_HOST, getHostFromRequest(httpServletRequest));
        span.setTag(FIELD_API, httpServletRequest.getRequestURI());
    }

    @Override
    public void onResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Span span) {
        span.setTag(FIELD_HTTP_CODE, httpServletResponse.getStatus());
    }

    @Override
    public void onError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Throwable exception, Span span) {
        span.setTag(FIELD_HTTP_CODE, httpServletResponse.getStatus());
    }

    @Override
    public void onTimeout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, long timeout, Span span) {
        // todo
    }

    private String getHostFromRequest(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getScheme()
                + "://"
                + httpServletRequest.getServerName()
                + ":"
                + httpServletRequest.getServerPort();
    }

}