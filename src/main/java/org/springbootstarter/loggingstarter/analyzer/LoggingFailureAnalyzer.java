package org.springbootstarter.loggingstarter.analyzer;

import org.springbootstarter.loggingstarter.exception.LoggingStartupException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class LoggingFailureAnalyzer extends AbstractFailureAnalyzer<LoggingStartupException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, LoggingStartupException cause) {
        return new FailureAnalysis(cause.getMessage(), "Valid arguments for logging properties are required.", cause);
    }
}
