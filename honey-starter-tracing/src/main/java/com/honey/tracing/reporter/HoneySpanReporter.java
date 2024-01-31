package com.honey.tracing.reporter;

import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.spi.Reporter;

public class HoneySpanReporter implements Reporter {

    public void report(JaegerSpan span) {
        // todo 打印链路日志
        // todo 暂不做任何事情
    }

    public void close() {

    }

}