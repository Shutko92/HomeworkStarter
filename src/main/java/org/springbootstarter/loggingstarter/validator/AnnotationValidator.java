package org.springbootstarter.loggingstarter.validator;

import org.springbootstarter.loggingstarter.annotation.ControllerLogger;
import org.springbootstarter.loggingstarter.annotation.ServiceLogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

public class AnnotationValidator implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

        checkControllerAnnotation(clazz);
        checkServiceAnnotation(clazz);

        return bean;
    }

    private void checkServiceAnnotation(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ServiceLogger.class) &&
                !clazz.isAnnotationPresent(Service.class)) {
            throw new IllegalStateException("Class " + clazz.getName() +
                    " must have @Service when using @ServiceLogger");
        }
    }

    private void checkControllerAnnotation(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ControllerLogger.class) &&
                !clazz.isAnnotationPresent(RestController.class)) {
            throw new IllegalStateException("Class " + clazz.getName() +
                    " must have @RestController when using @ControllerLogger");
        }
    }
}
