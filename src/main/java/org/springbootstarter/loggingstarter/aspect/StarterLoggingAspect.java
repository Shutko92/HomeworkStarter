package org.springbootstarter.loggingstarter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
public class StarterLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(StarterLoggingAspect.class);

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
        log.info("Controller: {} Called method {} with a path {}", controllerName, methodName, path);
    }

    @Before("servicePointcut()")
    public void loggingServices(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(","));

        if (params.isEmpty()) {
            params = "without params";
        }
        log.info("Service: {} Called method {} with params - {}", className, methodName, params);
    }

    @AfterReturning(value = "servicePointcut()", returning = "response")
    public void loggingServicesReturn(JoinPoint joinPoint, Object response) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        if (response == null) {
            log.info("{} from {} returns nothing through controller", methodName, className);
        } else if (response instanceof List<?>) {
            logListResponse(methodName, className, (List<?>) response);
        } else {
            log.info("{} from {} returns through controller: {}", methodName, className, response);
        }
    }

    private void logListResponse(String methodName, String className, List<?> responseList) {
        log.info("{} from {} returns list of objects through controller. Examples:", methodName, className);
        responseList.stream()
                .limit(10)
                .map(Object::toString)
                .forEach(log::info);
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
        log.warn("Exception intercepted {}, message: {}, controller: {}, method: {}",
                exception, message, className, methodName);
    }

    @AfterThrowing(value = "servicePointcut()", throwing = "ex")
    public void loggingServicesThrowing(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        log.warn("Exception intercepted in {}, with throwing - {}", className, ex.getMessage());
    }
}
