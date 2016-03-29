package com.vennetics.microservices.common.core.logging.aop;

import static com.vennetics.microservices.common.core.logging.LoggingConstants.INDENT;
import static com.vennetics.microservices.common.core.logging.LoggingConstants.NEW_LINE;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vennetics.microservices.common.core.logging.utils.CoreLogUtils;

import rx.Observable;

/**
 * Standard logger, uses reflection to debug method parameters.
 */
@Component
public class StandardPjpLogger implements IProceedingJoinPointLogger {

    private final ICoreParameterNameDiscoverer coreParameterNameDiscoverer;

    @Autowired(required = true)
    public StandardPjpLogger(final ICoreParameterNameDiscoverer coreParameterNameDiscoverer) {
        super();
        this.coreParameterNameDiscoverer = coreParameterNameDiscoverer;
    }

    @Override
    public void logException(final Logger logger,
                             final ProceedingJoinPoint pjp,
                             final Throwable exception) {
        final String description = getClassName(pjp) + ".";

        String message = exception.getMessage();

        if (StringUtils.isBlank(message)) {
            message = "Blank message on exception type " + exception.getClass().getName();
        }

        logger.debug("{} <<< Exception in method: {}{} Error Message: {}{}",
                     NEW_LINE,
                     description,
                     pjp.getSignature().getName(),
                     message,
                     NEW_LINE);
    }

    @Override
    public void logMethodEntry(final Logger logger, final ProceedingJoinPoint pjp) {

        final String description = getClassName(pjp) + ".";

        final String[] names = coreParameterNameDiscoverer.getParamNames(pjp);

        logger.debug("{} >>> {}{}{}{}",
                     NEW_LINE,
                     description,
                     pjp.getSignature().getName(),
                     parameters(names, pjp.getArgs()),
                     NEW_LINE);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void logMethodExit(final Logger logger,
                              final ProceedingJoinPoint pjp,
                              final Object returnValue) {

        final String description = getClassName(pjp) + ".";

        if (returnValue instanceof Observable) {
            final Observable observableReturnValue = (Observable) returnValue;

            // Cache the observable.
            // Future subscriptions to the same will not cause a
            // re-execution.
            //
            observableReturnValue.cache().subscribe(subscribeResult -> {
                final String returnAsString = CoreLogUtils.returnValueAsString(subscribeResult);

                logger.debug("{} <<< {}{}{}{}observableResult={}{}",
                             NEW_LINE,
                             description,
                             pjp.getSignature().getName(),
                             NEW_LINE,
                             INDENT,
                             returnAsString,
                             NEW_LINE);
            });
        } else {

            final String returnAsString = CoreLogUtils.returnValueAsString(returnValue);

            logger.debug("{} <<< {}{}{}{}result={}{}",
                         NEW_LINE,
                         description,
                         pjp.getSignature().getName(),
                         NEW_LINE,
                         INDENT,
                         returnAsString,
                         NEW_LINE);
        }

    }

    /** Log entry parameters */
    private static String parameters(final String[] names, final Object[] arguments) {

        final StringBuilder sb = new StringBuilder("(");
        if (arguments != null) {

            sb.append(NEW_LINE);

            CoreLogUtils.appendNamedParameters(sb, names, arguments, 1);

        }

        sb.append(')');
        return sb.toString();
    }

    /** Returns the real class name being invoked */
    private static String getClassName(final ProceedingJoinPoint pjp) {
        return pjp.getSignature().getDeclaringTypeName();
    }
}
