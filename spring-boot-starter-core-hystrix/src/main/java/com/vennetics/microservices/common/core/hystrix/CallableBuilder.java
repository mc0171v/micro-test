package com.vennetics.microservices.common.core.hystrix;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.MDC;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * Builder class for wrapping Callable objects with Mdc of the calling thread
 * and with Hystrix Context information.
 *
 * @param <T>
 *            return type of the {@link Callable}
 */
public final class CallableBuilder<T> {

    private final Callable<T> wrapped;

    private CallableBuilder(final Callable<T> wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * @return the callable
     */
    public Callable<T> build() {
        return wrapped;
    }

    /**
     * Create a builder for wrapping the supplied callable with the calling
     * thread state.
     *
     * @param callable
     * @return the builder
     */
    public static <T> CallableBuilder<T> wrap(final Callable<T> callable) {
        return new CallableBuilder<T>(callable);
    }

    /**
     * @return a Callable that will execute asynchronously with the hystrix
     *         context, threadl local and MDC.
     */
    public CallableBuilder<T> withAllRequestState() {
        return new CallableBuilder<T>(wrapped).withHystrixContext().withMdc();
    }

    /**
     * @return a Callable that will execute asynchronously with the hystrix
     *         context.
     */
    public CallableBuilder<T> withHystrixContext() {

        return new CallableBuilder<T>(new HystrixContextWrapper<T>(wrapped));
    }

    /**
     * @return a Callable that will execute asynchronously with the MDC.
     */
    public CallableBuilder<T> withMdc() {

        return new CallableBuilder<T>(new MdcWrapper<T>(wrapped));
    }

    private static class MdcWrapper<T> implements Callable<T> {

        private final Callable<T> actual;

        private final Map<String, String> parentMDC;

        MdcWrapper(final Callable<T> actual) {
            // Take copy of marent MDC on creation (pulling from the creating
            // thread).
            parentMDC = MDC.getCopyOfContextMap();
            this.actual = actual;
        }

        @Override
        public T call() throws Exception { // NOPMD
            // Take copy of existing thread state on run
            final Map<String, String> childMDC = MDC.getCopyOfContextMap();


            try {

                MDC.setContextMap(parentMDC);

                return actual.call();
            } finally {

                MDC.setContextMap(childMDC);
            }

        }

    }

    private static class HystrixContextWrapper<T> implements Callable<T> {

        private final HystrixRequestContext parentThreadState;

        private final Callable<T> actual;

        HystrixContextWrapper(final Callable<T> actual) {
            parentThreadState = HystrixRequestContext.getContextForCurrentThread();
            this.actual = actual;
        }

        @Override
        public T call() throws Exception { // NOPMD
            // Take copy of existing thread state
            final HystrixRequestContext existingState = HystrixRequestContext.getContextForCurrentThread();

            try {
                // set the state of this thread to that of its parent
                HystrixRequestContext.setContextOnCurrentThread(parentThreadState);

                return actual.call();
            } finally {

                // restore this thread back to its original state
                HystrixRequestContext.setContextOnCurrentThread(existingState);
            }

        }

    }

}
