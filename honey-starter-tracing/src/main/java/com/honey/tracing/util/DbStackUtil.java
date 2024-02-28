package com.honey.tracing.util;

import io.jaegertracing.internal.JaegerSpan;

import java.util.HashMap;
import java.util.Map;

import static com.honey.tracing.constant.CommonConstants.*;

/**
 * dbStack记录工具。
 */
public class DbStackUtil {

    /**
     * 生成操作数据库时的dbStack。
     */
    public static Map<String, Object> assembleDbStack(JaegerSpan span) {
        Map<String, Object> requestStack = new HashMap<>();
        requestStack.put(LOG_EVENT_KIND, LOG_EVENT_KIND_DB_STACK);
        requestStack.put(FIELD_DB_SERVER, span.getTags().get(FIELD_DB_SERVER));
        requestStack.put(FIELD_DB_NAME, span.getTags().get(FIELD_DB_NAME));
        requestStack.put(FIELD_SQL_TEXT, span.getTags().get(FIELD_SQL_TEXT));
        requestStack.put(FIELD_SQL_PARAMS, span.getTags().get(FIELD_SQL_PARAMS));
        requestStack.put(FIELD_SQL_DURATION, span.getDuration());
        requestStack.put(FIELD_SQL_TIMESTAMP, span.getStart());
        return requestStack;
    }

}