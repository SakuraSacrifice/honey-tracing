package com.honey.tracing.async;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * 依赖{@link TransmittableThreadLocal}的{@link Scope}实现。
 */
public class HoneyTtlScope implements Scope {

    private final HoneyTtlScopeManager scopeManager;
    private final Span wrapped;
    private final HoneyTtlScope toRestore;

    HoneyTtlScope(HoneyTtlScopeManager scopeManager, Span wrapped) {
        this.scopeManager = scopeManager;
        this.wrapped = wrapped;
        this.toRestore = scopeManager.tlsScope.get();
        scopeManager.tlsScope.set(this);
    }

    @Override
    public void close() {
        if (scopeManager.tlsScope.get() != this) {
            return;
        }
        scopeManager.tlsScope.set(toRestore);
    }

    Span span() {
        return wrapped;
    }

}