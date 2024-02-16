package com.honey.tracing.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 分布式链路追踪配置属性类。
 */
@ConfigurationProperties("honey.tracing")
public class HoneyTracingProperties {

    private boolean enabled;

    private HttpUrlProperties httpUrl = new HttpUrlProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HttpUrlProperties getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(HttpUrlProperties httpUrl) {
        this.httpUrl = httpUrl;
    }

    public static class HttpUrlProperties {
        /**
         * 按照/url1,/url2这样配置。
         */
        private String urlPattern = "/*";
        /**
         * 按照/url1|/honey.*这样配置。
         */
        private String skipPattern = "";

        public String getUrlPattern() {
            return urlPattern;
        }

        public void setUrlPattern(String urlPattern) {
            this.urlPattern = urlPattern;
        }

        public String getSkipPattern() {
            return skipPattern;
        }

        public void setSkipPattern(String skipPattern) {
            this.skipPattern = skipPattern;
        }
    }

}