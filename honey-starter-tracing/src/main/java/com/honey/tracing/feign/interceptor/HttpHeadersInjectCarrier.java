package com.honey.tracing.feign.interceptor;

import io.opentracing.propagation.TextMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class HttpHeadersInjectCarrier implements TextMap {

    private final Map<String, Collection<String>> httpHeaders;

    public HttpHeadersInjectCarrier(Map<String, Collection<String>> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    @Override
    public void put(String key, String value) {
        httpHeaders.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException();
    }

}