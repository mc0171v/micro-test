package com.vennetics.microservices.common.core.hystrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public class CallableBuilderTest {

    private ExecutorService executor;

    @Before
    public void init() {
        executor = Executors.newSingleThreadExecutor();
    }

    @After
    public void destroy() {
        executor.shutdown();
    }

    @Test
    public void testMdcWrap() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        MDC.put("ping", "pong");

        final Callable<String> testCallable = () -> {
            atomicBoolean.set(MDC.get("ping").equals("pong"));
            countDownLatch.countDown();
            return "blah";

        };

        final Callable<String> wrapped = CallableBuilder.wrap(testCallable).withMdc().build();

        final Future<String> result = executor.submit(wrapped);

        countDownLatch.await(1, TimeUnit.SECONDS);

        assertEquals("blah", result.get());

        assertTrue(atomicBoolean.get());

        MDC.remove("ping");
    }

    @Test
    public void testHystrixWrap() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        final HystrixRequestContext context = HystrixRequestContext.initializeContext();

        final Callable<String> testCallable = () -> {
            atomicBoolean.set(HystrixRequestContext.isCurrentThreadInitialized());
            countDownLatch.countDown();
            return "blah";

        };

        final Callable<String> wrapped = CallableBuilder.wrap(testCallable)
                                                        .withHystrixContext()
                                                        .build();

        final Future<String> result = executor.submit(wrapped);

        countDownLatch.await(1, TimeUnit.SECONDS);

        assertEquals("blah", result.get());

        assertTrue(atomicBoolean.get());

        context.shutdown();
    }

    @Test
    public void testAllThree() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean hystrixCheck = new AtomicBoolean(false);
        final AtomicBoolean mdcCheck = new AtomicBoolean(false);

        final HystrixRequestContext context = HystrixRequestContext.initializeContext();
        MDC.put("ping", "pong");

        final Callable<String> testCallable = () -> {
            hystrixCheck.set(HystrixRequestContext.isCurrentThreadInitialized());
            mdcCheck.set(MDC.get("ping").equals("pong"));
            countDownLatch.countDown();
            return "blah";

        };

        final Callable<String> wrapped = CallableBuilder.wrap(testCallable)
                                                        .withHystrixContext()
                                                        .withMdc()
                                                        .build();

        final Future<String> result = executor.submit(wrapped);

        countDownLatch.await(1, TimeUnit.SECONDS);

        assertEquals("blah", result.get());

        assertTrue(hystrixCheck.get());
        assertTrue(mdcCheck.get());

        context.shutdown();

        MDC.remove("ping");
    }

    @Test
    public void testComposite() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean hystrixCheck = new AtomicBoolean(false);
        final AtomicBoolean mdcCheck = new AtomicBoolean(false);

        final HystrixRequestContext context = HystrixRequestContext.initializeContext();
        MDC.put("ping", "pong");

        final Callable<String> testCallable = () -> {
            hystrixCheck.set(HystrixRequestContext.isCurrentThreadInitialized());
            mdcCheck.set(MDC.get("ping").equals("pong"));
            countDownLatch.countDown();
            return "blah";

        };

        final Callable<String> wrapped = CallableBuilder.wrap(testCallable)
                                                        .withAllRequestState()
                                                        .build();

        final Future<String> result = executor.submit(wrapped);

        countDownLatch.await(1, TimeUnit.SECONDS);

        assertEquals("blah", result.get());

        assertTrue(hystrixCheck.get());
        assertTrue(mdcCheck.get());

        context.shutdown();

        MDC.remove("ping");
    }

    @Test
    public void testNullMdcParent() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        final Callable<String> testCallable = () -> {
            atomicBoolean.set(MDC.get("ping") != null && MDC.get("ping").equals("pong"));
            countDownLatch.countDown();
            return "blah";

        };

        final Callable<String> wrapped = CallableBuilder.wrap(testCallable).withMdc().build();

        final Future<String> result = executor.submit(wrapped);

        countDownLatch.await(1, TimeUnit.SECONDS);

        assertEquals("blah", result.get());

        // Won't have been set
        assertFalse(atomicBoolean.get());
    }

}
