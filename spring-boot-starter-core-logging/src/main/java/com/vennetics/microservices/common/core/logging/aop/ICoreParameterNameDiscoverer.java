package com.vennetics.microservices.common.core.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Wraps spring aop param name discoverer for injection as a spring interface.
 * Used in our PJP loggers to determine the names of parameters. Requires
 * compiling with debug info enabled.
 */
public interface ICoreParameterNameDiscoverer {

    /**
     * Get the param names for a pjp. In some cases (e.g. generics) it won't be
     * able to discover the name in which case arg[1] syntax is used.
     *
     * @param pjp
     * @return the names in argument order.
     */
    String[] getParamNames(ProceedingJoinPoint pjp);

}
