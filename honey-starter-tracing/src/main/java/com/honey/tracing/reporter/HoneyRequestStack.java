package com.honey.tracing.reporter;

import com.honey.tracing.exception.HoneyTracingException;
import io.jaegertracing.internal.LogData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.honey.tracing.constant.CommonConstants.*;
import static com.honey.tracing.reporter.HoneySpanReportEntity.SCALE;

public class HoneyRequestStack {

    private String subSpanId;
    private String subHttpCode;
    private String subTimestamp;
    private String subDuration;
    private String subHost;

    private HoneyRequestStack() {

    }

    public String getSubSpanId() {
        return subSpanId;
    }

    public void setSubSpanId(String subSpanId) {
        this.subSpanId = subSpanId;
    }

    public String getSubHttpCode() {
        return subHttpCode;
    }

    public void setSubHttpCode(String subHttpCode) {
        this.subHttpCode = subHttpCode;
    }

    public String getSubTimestamp() {
        return subTimestamp;
    }

    public void setSubTimestamp(String subTimestamp) {
        this.subTimestamp = subTimestamp;
    }

    public String getSubDuration() {
        return subDuration;
    }

    public void setSubDuration(String subDuration) {
        this.subDuration = subDuration;
    }

    public String getSubHost() {
        return subHost;
    }

    public void setSubHost(String subHost) {
        this.subHost = subHost;
    }

    public static class HoneyRequestStackBuilder {
        private LogData logData;

        private HoneyRequestStackBuilder() {

        }

        public static HoneyRequestStackBuilder builder() {
            return new HoneyRequestStackBuilder();
        }

        public HoneyRequestStackBuilder withLogData(LogData logData) {
            this.logData = logData;
            return this;
        }

        public HoneyRequestStack build() {
            if (logData == null || logData.getFields() == null) {
                throw new HoneyTracingException();
            }
            Map<String, ?> logDataFields = logData.getFields();
            HoneyRequestStack honeyRequestStack = new HoneyRequestStack();
            honeyRequestStack.subSpanId = (String) logDataFields.get(FIELD_SUB_SPAN_ID);
            honeyRequestStack.subHttpCode = String.valueOf(logDataFields.get(FIELD_SUB_HTTP_CODE));
            honeyRequestStack.subTimestamp = new BigDecimal(String.valueOf(logDataFields.get(FIELD_SUB_TIMESTAMP)))
                    .divide(BigDecimal.valueOf(1000), SCALE , RoundingMode.DOWN).toString();
            honeyRequestStack.subDuration = new BigDecimal(String.valueOf(logDataFields.get(FIELD_SUB_DURATION)))
                    .divide(BigDecimal.valueOf(1000), SCALE , RoundingMode.DOWN).toString();
            honeyRequestStack.subHost = (String) logDataFields.get(FIELD_SUB_HOST);
            return honeyRequestStack;
        }
    }

}