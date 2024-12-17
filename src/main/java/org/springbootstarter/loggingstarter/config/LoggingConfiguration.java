package org.springbootstarter.loggingstarter.config;

import org.springbootstarter.loggingstarter.aspect.StarterLoggingAspect;
import org.springbootstarter.loggingstarter.validator.AnnotationValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "logging-api", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingConfiguration {
    private final LoggingProperties properties;
    //проперти используются через ConditionalOnProperty и как параметр в LoggingLevelManager

    public LoggingConfiguration(LoggingProperties properties) {
        this.properties = properties;
    }

    @Bean
    StarterLoggingAspect starterLoggingAspect(LoggingLevelManager loggingLevelManager) {
        return new StarterLoggingAspect(loggingLevelManager);
    }

    @Bean
    AnnotationValidator annotationValidator() {
        return new AnnotationValidator();
    }

    @Bean
    LoggingLevelManager loggingLevelManager() {
        return new LoggingLevelManager(properties.getLoggingLevel());
    }
}
