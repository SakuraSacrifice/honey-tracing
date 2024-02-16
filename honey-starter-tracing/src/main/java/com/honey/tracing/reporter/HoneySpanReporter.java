package com.honey.tracing.reporter;

import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.spi.Reporter;
import io.opentracing.tag.Tags;

public class HoneySpanReporter implements Reporter {

    public void report(JaegerSpan span) {
        if (Tags.SPAN_KIND_CLIENT.equals(span.getTags().get(Tags.SPAN_KIND.getKey()))) {
            return;
        }

        System.out.println(HoneySpanReportEntity.HoneySpanReportEntityBuilder
                .builder()
                .withSpan(span)
                .build()
                .toPrintString());
    }

    public void close() {

    }

}