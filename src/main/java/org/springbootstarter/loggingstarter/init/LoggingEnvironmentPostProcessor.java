package org.springbootstarter.loggingstarter.init;

import org.apache.commons.logging.Log;
import org.springbootstarter.loggingstarter.exception.LoggingStartupException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;
import java.util.Set;

public class LoggingEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private final Log log;

    public LoggingEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(LoggingEnvironmentPostProcessor.class);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        log.info("Calling LoggingEnvironmentPostProcessor");

        enabledPropertyEnvironment(environment);
        loggingLevelPropertyEnvironment(environment);
    }

    private void loggingLevelPropertyEnvironment(ConfigurableEnvironment environment) {
        String levelPropertyValue = String.valueOf(environment.getProperty("spring.logging-api.logging-level"));
        Set<String> validLogLevels = Set.of("WARN", "INFO", "DEBUG", "TRACE", "ERROR");

        if (validLogLevels.contains(levelPropertyValue)) {
            environment.getPropertySources().addFirst(new MapPropertySource("customLoggingLevels", Map.of(
                    "logging.level.org.springbootstarter.loggingstarter.aspect", levelPropertyValue
            )));

            log.info("Logging level of logging starter set to " + levelPropertyValue);
        } else {
            throw new LoggingStartupException("Error checking property 'spring.logging-api.logging-level'");
        }
    }

    private static void enabledPropertyEnvironment(ConfigurableEnvironment environment) {
        String enabledPropertyValue = environment.getProperty("spring.logging-api.enabled");

        boolean isBoolValue = Boolean.TRUE.toString().equals(enabledPropertyValue) ||
                Boolean.FALSE.toString().equals(enabledPropertyValue);

        if (!isBoolValue) {
            throw new LoggingStartupException("Error checking property 'spring.logging-api.enabled'");
        }
    }
}
