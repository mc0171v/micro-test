package com.vennetics.microservices.common.core.hystrix;

import java.util.concurrent.Callable;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

/**
 * Custom hystrix concurrency strategy for copying thread local information into
 * the command execution thread.
 */
public class CoreHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    @Override
    public <T> Callable<T> wrapCallable(final Callable<T> callable) {

        return CallableBuilder.wrap(callable).withAllRequestState().build();
    }
}
