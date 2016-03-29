package com.vennetics.microservices.common.core.hystrix;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.mockito.Mockito;

public class CoreHystrixConcurrencyStrategyTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldWrapCallable() {

        assertTrue(new CoreHystrixConcurrencyStrategy().wrapCallable(Mockito.mock(Callable.class)) instanceof Callable);
    }

}
