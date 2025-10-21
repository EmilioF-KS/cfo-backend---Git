package com.cfo.reporting.controller;

import com.cfo.reporting.exception.RepositoryExceptionEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;



@Component
public class RepositoryErrorAspect {
    //private static final Logger logger = (Logger) LoggerFactory.getLogger(RepositoryErrorAspect.class);

    public Object logRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String repositoryName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        try {

            return joinPoint.proceed();
        } catch(InvalidDataAccessResourceUsageException e) {
            if (e.getMessage().contains("unable to find column position by name")) {
                System.out.println("Error in column  : {}.{}()"+repositoryName+methodName+e.getMessage());
            }
            throw e;
        }

    }
    @EventListener
    public void handleRepositoryException(RepositoryExceptionEvent event) {
        System.out.println("Error in column  : {}.{}()"+event.getRepositoryName()+event.getMethodName()+event.getException().getMessage());

    }

}
