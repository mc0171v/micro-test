package com.vennetics.microservices.common.core.logging.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Pointcuts used by the logging advisors
 */
@Aspect
@Component
public class LoggingPointcuts {

    /**
     * Any Vennetics implemented bean that isn't the logger itself to avoid
     * circular dependencies.
     */
    @Pointcut("(inAnyVenneticsPackage() && !inVenneticsLoggingPackage() )")
    public void inLoggingPointCut() {
        // Pointcut method - no implementation
    }

    /**
     * Implementation (not the interface) is in a vennetics package.
     */
    @Pointcut("execution(* com.vennetics..*.*(..))")
    public void inAnyVenneticsPackage() {
        // Pointcut method - no implementation
    }

    /**
     * Implementation (not the interface) is NOT in a vennetics package.
     */
    @Pointcut("execution(* com.vennetics.microservices.common.core.logging..*.*(..))")
    public void inVenneticsLoggingPackage() {
        // Pointcut method - no implementation
    }
}
