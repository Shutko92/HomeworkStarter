package org.springbootstarter.loggingstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.logging-api")
public class LoggingProperties {
    private boolean enabled;
    private String loggingLevel;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }
}
