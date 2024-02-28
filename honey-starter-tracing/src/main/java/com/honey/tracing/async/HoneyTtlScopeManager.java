package com.honey.tracing.async;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;

/**
 * 基于{@link TransmittableThreadLocal}的{@link ScopeManager}实现。
 */
public class HoneyTtlScopeManager implements ScopeManager {

    final InheritableThreadLocal<HoneyTtlScope> tlsScope = new TransmittableThreadLocal<>();

    @Override
    public Scope activate(Span span) {
        return new HoneyTtlScope(this, span);
    }

    @Override
    public Span activeSpan() {
        HoneyTtlScope scope = tlsScope.get();
        return scope == null ? null : scope.span();
    }

}