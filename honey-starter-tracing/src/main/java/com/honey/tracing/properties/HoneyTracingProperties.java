package com.honey.tracing.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 分布式链路追踪配置属性类。
 */
@ConfigurationProperties("honey.tracing")
public class HoneyTracingProperties {

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}