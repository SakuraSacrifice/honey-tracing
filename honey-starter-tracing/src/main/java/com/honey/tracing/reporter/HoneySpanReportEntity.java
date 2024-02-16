package com.honey.tracing.reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honey.tracing.exception.HoneyTracingException;
import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.LogData;
import io.jaegertracing.internal.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.honey.tracing.constant.CommonConstants.*;

public class HoneySpanReportEntity {

    public static final Integer SCALE = 0;

    private String traceId;
    private String spanId;
    private String parentSpanId;
    private String timestamp;
    private String duration;
    private String httpCode;
    private String host;
    private List<HoneyRequestStack> requestStacks = new ArrayList<>();

    private HoneySpanReportEntity() {

    }

    public String toPrintString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public void addRequestStack(HoneyRequestStack honeyRequestStack) {
        requestStacks.add(honeyRequestStack);
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(String httpCode) {
        this.httpCode = httpCode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<HoneyRequestStack> getRequestStacks() {
        return requestStacks;
    }

    public void setRequestStacks(List<HoneyRequestStack> requestStacks) {
        this.requestStacks = requestStacks;
    }

    public static class HoneySpanReportEntityBuilder {
        private JaegerSpan span;

        private HoneySpanReportEntityBuilder() {

        }

        public static HoneySpanReportEntityBuilder builder() {
            return new HoneySpanReportEntityBuilder();
        }

        public HoneySpanReportEntityBuilder withSpan(JaegerSpan span) {
            this.span = span;
            return this;
        }

        public HoneySpanReportEntity build() {
            if (span == null) {
                throw new HoneyTracingException();
            }

            HoneySpanReportEntity honeySpanReportEntity = new HoneySpanReportEntity();

            honeySpanReportEntity.traceId = span.context().getTraceId();
            honeySpanReportEntity.spanId = span.context().toSpanId();
            honeySpanReportEntity.parentSpanId = Utils.to16HexString(span.context().getParentId());
            honeySpanReportEntity.timestamp = new BigDecimal(span.getStart())
                    .divide(BigDecimal.valueOf(1000), SCALE , RoundingMode.DOWN).toString();
            honeySpanReportEntity.duration = new BigDecimal(span.getDuration())
                    .divide(BigDecimal.valueOf(1000), SCALE , RoundingMode.DOWN).toString();

            Map<String, Object> spanTags = span.getTags();
            honeySpanReportEntity.httpCode = String.valueOf(spanTags.get(FIELD_HTTP_CODE));
            honeySpanReportEntity.host = (String) spanTags.get(FIELD_HOST);

            List<LogData> spanLogs = span.getLogs();
            if (span.getLogs() != null) {
                spanLogs.forEach(handleLogData(honeySpanReportEntity));
            }

            return honeySpanReportEntity;
        }

        private Consumer<LogData> handleLogData(HoneySpanReportEntity honeySpanReportEntity) {
            return new Consumer<LogData>() {
                @Override
                public void accept(LogData logData) {
                    if (LOG_EVENT_KIND_REQUEST_STACK.equals(logData.getFields().get(LOG_EVENT_KIND))) {
                        HoneyRequestStack honeyRequestStack = HoneyRequestStack.HoneyRequestStackBuilder
                                .builder()
                                .withLogData(logData)
                                .build();
                        honeySpanReportEntity.addRequestStack(honeyRequestStack);
                    }
                }
            };
        }
    }

}