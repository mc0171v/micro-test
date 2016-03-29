package com.vennetics.microservices.common.core.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.vennetics.microservices.common.core.logging.LoggingConstants;

/**
 * Advice that wraps calls with logging entry, exit and on exception cases.
 */
@Component
@Aspect
public class CoreLoggerAdvice implements Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CoreLoggerAdvice.class.getName());

    private final IProceedingJoinPointLogger proceedingJoinPointLogger;

    private boolean overrideLogConfig;

    @Autowired
    public CoreLoggerAdvice(@Qualifier("standardPjpLogger") final IProceedingJoinPointLogger proceedingJoinPointLogger) {
        super();
        this.proceedingJoinPointLogger = proceedingJoinPointLogger;
        overrideLogConfig = true;
        logger.debug("Initialised CoreLoggerAdvice");
    }

    /**
     * Log the invocation using a logger named by the target object.
     *
     * @param pjp
     * @return the result from the target object.
     * @throws Throwable
     */
    @Around("LoggingPointcuts.inLoggingPointCut()")
    // Allow throwable as needed by AOP.
    // Allow targetObjectLogger as used as a member rather than static
    // so logging can be enabled/disabled on the fly for the target object.
    @SuppressWarnings({ "squid:S00112", "squid:S1312" })
    public Object doLogging(final ProceedingJoinPoint pjp) throws Throwable {

        // Base logger on target object if available
        final Logger targetObjectLogger = LoggerFactory.getLogger(pjp.getTarget().getClass());

        if (isDebugEnabled(targetObjectLogger)) {
            try {
                tryLogEntry(pjp, targetObjectLogger);

                final Object result = pjp.proceed();

                tryLogExit(pjp, targetObjectLogger, result);

                return result;
            } catch (final Exception e) {

                tryLogException(pjp, targetObjectLogger, e);

                throw e;
            }
        }

        // Continue
        return pjp.proceed();
    }

    /**
     * Protected to allow unit test override as we can't access logger APIs for
     * mocking.
     *
     * @param targetObjectLogger
     * @return if enabled
     */
    protected boolean isDebugEnabled(final Logger targetObjectLogger) {
        if (overrideLogConfig) {

            logger.trace("Debug over-ride enabled");
            return true;
        }

        logger.trace("Debug over-ride disabled");
        return targetObjectLogger.isDebugEnabled();
    }

    private void tryLogException(final ProceedingJoinPoint pjp,
                                 final Logger targetObjectLogger,
                                 final Exception e) {
        try {
            proceedingJoinPointLogger.logException(targetObjectLogger, pjp, e);
        } catch (final RuntimeException e2) {
            logger.warn("Failed to log method exception", e2);
        }
    }

    private void tryLogExit(final ProceedingJoinPoint pjp,
                            final Logger targetObjectLogger,
                            final Object result) {
        try {
            proceedingJoinPointLogger.logMethodExit(targetObjectLogger, pjp, result);
        } catch (final RuntimeException e) {
            logger.warn("Failed to log method exit.", e);
        }
    }

    private void tryLogEntry(final ProceedingJoinPoint pjp, final Logger targetObjectLogger) {
        try {
            proceedingJoinPointLogger.logMethodEntry(targetObjectLogger, pjp);
        } catch (final RuntimeException e) {
            logger.warn("Failed to log method entry.", e);
        }
    }

    public void setOverrideLogConfig(final boolean overrideLogConfig) {
        this.overrideLogConfig = overrideLogConfig;
    }

    @Override
    public int getOrder() {

        return LoggingConstants.LOGGING_ADVICE_ORDER;
    }

}
