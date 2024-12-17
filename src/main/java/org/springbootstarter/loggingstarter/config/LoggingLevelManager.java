package org.springbootstarter.loggingstarter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springbootstarter.loggingstarter.exception.LoggingStartupException;

public class LoggingLevelManager {
    private static final Logger log = LoggerFactory.getLogger(LoggingLevelManager.class);
    private final String loggingLevel;

    public LoggingLevelManager(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public void log(String message) {
        switch (loggingLevel) {
            case "DEBUG"-> log.debug(message);
            case "INFO"-> log.info(message);
            case "WARN"-> log.warn(message);
            case "ERROR"-> log.error(message);
            case "TRACE"-> log.trace(message);
            default-> throw new LoggingStartupException("Error checking property 'logging-api.logging-level'");
        }
    }
}
