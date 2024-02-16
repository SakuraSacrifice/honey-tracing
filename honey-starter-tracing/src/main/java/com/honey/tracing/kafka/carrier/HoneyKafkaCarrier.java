package com.honey.tracing.kafka.carrier;

import io.opentracing.propagation.TextMap;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HoneyKafkaCarrier implements TextMap {

    private final Headers headers;

    public HoneyKafkaCarrier(Headers headers) {
        this.headers = headers;
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        Map<String, String> headerMap = new HashMap<>();
        for (Header header : headers) {
            headerMap.put(header.key(), new String(header.value()));
        }
        return headerMap.entrySet().iterator();
    }

    @Override
    public void put(String key, String value) {
        headers.add(key, value.getBytes());
    }

}