package org.springbootstarter.loggingstarter.config;

import org.springbootstarter.loggingstarter.aspect.StarterLoggingAspect;
import org.springbootstarter.loggingstarter.validator.AnnotationValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.logging-api", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingConfiguration {

    @Bean
    StarterLoggingAspect starterLoggingAspect() {
        return new StarterLoggingAspect();
    }

    @Bean
    AnnotationValidator annotationValidator() {
        return new AnnotationValidator();
    }
}
