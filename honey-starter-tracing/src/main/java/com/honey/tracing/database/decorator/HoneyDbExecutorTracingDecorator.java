package com.honey.tracing.database.decorator;

import io.opentracing.Span;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Invocation;

/**
 * 作用于{@link Executor}的装饰器。
 */
public interface HoneyDbExecutorTracingDecorator {

    void onExecute(Invocation invocation, Span span);

    void onFinish(Invocation invocation, Span span);

    void onError(Invocation invocation, Exception exception, Span span);

}