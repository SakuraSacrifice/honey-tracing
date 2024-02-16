package com.honey.tracing.util;

import io.jaegertracing.internal.JaegerSpan;

import java.util.HashMap;
import java.util.Map;

import static com.honey.tracing.constant.CommonConstants.*;

/**
 * requestStack记录工具类。
 */
public class RequestStackUtil {

    /**
     * 生成使用HTTP方式访问下游的requestStack。
     */
    public static Map<String, Object> assembleRequestStack(JaegerSpan span) {
        Map<String, Object> requestStack = new HashMap<>();
        requestStack.put(LOG_EVENT_KIND, LOG_EVENT_KIND_REQUEST_STACK);
        requestStack.put(FIELD_SUB_SPAN_ID, span.context().toSpanId());
        requestStack.put(FIELD_SUB_HTTP_CODE, span.getTags().get(FIELD_HTTP_CODE));
        requestStack.put(FIELD_SUB_TIMESTAMP, span.getStart());
        requestStack.put(FIELD_SUB_DURATION, span.getDuration());
        requestStack.put(FIELD_SUB_HOST, span.getTags().get(FIELD_HOST));
        return requestStack;
    }

}