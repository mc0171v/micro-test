package com.vennetics.microservices.common.core.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;

/**
 * Provides utility methods for creating logging information using the AspectJ
 * ProceedingJoinPoint type.
 */
public interface IProceedingJoinPointLogger {

    /**
     * Log an exception thrown from the pjp invocation.
     * 
     * @param logger
     * @param pjp
     * @param exception
     */
    void logException(final Logger logger, final ProceedingJoinPoint pjp, final Throwable exception);

    /**
     * Log method entry to the PJP.
     * 
     * @param logger
     * @param pjp
     */
    void logMethodEntry(final Logger logger, final ProceedingJoinPoint pjp);

    /**
     * Log method exit from the PJP.
     * 
     * @param logger
     * @param pjp
     * @param returnValue
     */
    void logMethodExit(final Logger logger, final ProceedingJoinPoint pjp, final Object returnValue);
}
