package com.honey.tracing.database.interceptor;

import com.honey.tracing.database.decorator.HoneyDbExecutorTracingDecorator;
import com.honey.tracing.util.DbStackUtil;
import io.jaegertracing.internal.JaegerSpan;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

import static com.honey.tracing.constant.CommonConstants.HONEY_DB_NAME;

/**
 * MyBaits作用于{@link Executor}用于记录{@link Span}的拦截器。
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class HoneyDbExecutorTracingInterceptor implements Interceptor {

    private final Tracer tracer;
    private final List<HoneyDbExecutorTracingDecorator> decorators;

    public HoneyDbExecutorTracingInterceptor(Tracer tracer, List<HoneyDbExecutorTracingDecorator> decorators) {
        this.tracer = tracer;
        this.decorators = decorators;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (null == tracer.activeSpan()) {
            return invocation.proceed();
        }

        Span span = tracer.buildSpan(HONEY_DB_NAME)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .start();
        for (HoneyDbExecutorTracingDecorator decorator : decorators) {
            try {
                decorator.onExecute(invocation, span);
            } catch (Exception e) {
                // do nothing
            }
        }

        Object result;
        try (Scope scope = tracer.activateSpan(span)) {
            try {
                result = invocation.proceed();
            } catch (Exception e1) {
                for (HoneyDbExecutorTracingDecorator decorator : decorators) {
                    try {
                        decorator.onError(invocation, e1, span);
                    } catch (Exception e2) {
                        // do nothing
                    }
                }
                throw e1;
            }

            for (HoneyDbExecutorTracingDecorator decorator : decorators) {
                try {
                    decorator.onFinish(invocation, span);
                } catch (Exception e) {
                    // do nothing
                }
            }
        } finally {
            span.finish();
            tracer.activeSpan().log(DbStackUtil.assembleDbStack((JaegerSpan) span));
        }

        return result;
    }

}