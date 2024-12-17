package org.springbootstarter.loggingstarter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springbootstarter.loggingstarter.config.LoggingLevelManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
public class StarterLoggingAspect {
    private final LoggingLevelManager logger;

    public StarterLoggingAspect(LoggingLevelManager logger) {
        this.logger = logger;
    }

    @Pointcut("@within(org.springbootstarter.loggingstarter.annotation.ControllerLogger)")
    public void controllerPointcut() {
    }

    @Pointcut("@within(org.springbootstarter.loggingstarter.annotation.ServiceLogger)")
    public void servicePointcut() {
    }

    @Before("controllerPointcut()")
    public void loggingControllers(JoinPoint joinPoint) {
        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String path = request.getRequestURI();
        logger.log("Controller: %s Called method %s with a path %s".formatted(controllerName, methodName, path));
    }

    @Before("servicePointcut()")
    public void loggingServices(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(","));

        if (params.isEmpty()) {
            params = "without params";
        }
        logger.log("Service: %s Called method %s with params - %s".formatted(className, methodName, params));
    }

    @AfterReturning(value = "servicePointcut()", returning = "response")
    public void loggingServicesReturn(JoinPoint joinPoint, Object response) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        if (response == null) {
            logger.log("%s from %s returns nothing through controller".formatted(methodName, className));
        } else if (response instanceof List<?> responseList) {
            logger.log("%s from %s returns list of objects through controller. Examples:".formatted(methodName, className));
            responseList.stream()
                    .limit(10)
                    .map(Object::toString)
                    .forEach(logger::log);
        } else {
            logger.log("%s from %s returns through controller: %s".formatted(methodName, className, response));
        }
    }

    @After(value = "@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void loggingControllerAdvice(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        StringBuilder exception = new StringBuilder();
        StringBuilder message = new StringBuilder();
        Optional<Throwable> exceptionsOptional = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Throwable)
                .map(arg -> (Throwable) arg)
                .findFirst();
        if (exceptionsOptional.isPresent()) {
            exception.append(exceptionsOptional.get().getClass().getName());
            message.append(exceptionsOptional.get().getMessage());
        }
        logger.log("Exception intercepted %s, message: %s, controller: %s, method: %s"
                .formatted(exception, message, className, methodName));
    }

    @AfterThrowing(value = "servicePointcut()", throwing = "ex")
    public void loggingServicesThrowing(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        logger.log("Exception intercepted in %s, with throwing - %s".formatted(className, ex.getMessage()));
    }
}
