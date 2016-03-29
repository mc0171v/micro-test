package com.vennetics.microservices.common.core.logging.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Service;

/**
 * Wraps default Spring name discovery behaviour with check if a class is an
 * interface. When it is the names are obtained from the target class declared
 * method rather than the interface so we don't log the AOP proxy details.
 */
@Service
public class CoreParameterNameDiscoverer implements ICoreParameterNameDiscoverer {

    private static final Logger logger = LoggerFactory.getLogger(CoreParameterNameDiscoverer.class);

    private final ParameterNameDiscoverer parameterNameDiscoverer;

    @Autowired(required = true)
    public CoreParameterNameDiscoverer(final ParameterNameDiscoverer parameterNameDiscoverer) {
        super();
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Override
    public String[] getParamNames(final ProceedingJoinPoint pjp) {

        // Get from signature
        final MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = pjp.getTarget()
                            .getClass()
                            .getDeclaredMethod(pjp.getSignature().getName(),
                                               method.getParameterTypes());
            } catch (final SecurityException e) {
                logger.trace("SecurityException Failed to get param names ", e);
            } catch (final NoSuchMethodException e) {

                // Can happen with generics. Don't want this appearing at debug
                // level every time we use them.
                logger.trace("NoSuchMethodException Failed to get param names ", e);
            }
        }

        // If not possible e.g. due to generics fall back to spring
        // auto-discovery.
        return parameterNameDiscoverer.getParameterNames(method);
    }

}
