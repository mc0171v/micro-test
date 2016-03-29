package com.vennetics.microservices.common.core.logging.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vennetics.microservices.common.core.logging.LoggingConstants;
import com.vennetics.microservices.common.not.core.logging.ITestEcho;

@RunWith(MockitoJUnitRunner.class)
public class CoreLoggerAdviceTest {

    private static final Logger logger = LoggerFactory.getLogger(CoreLoggerAdviceTest.class);

    @Mock
    private IProceedingJoinPointLogger proceedingJoinPointLogger;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private ITestEcho target;

    private CoreLoggerAdvice testClass;


    @Before
    public void init() {
        testClass = new CoreLoggerAdvice(proceedingJoinPointLogger) {
            @Override
            protected boolean isDebugEnabled(final Logger targetObjectLogger) {
                return true;
            }
        };
        testClass.setOverrideLogConfig(true);
    }

    @Test
    public void shouldAllowDebugOverried() {

        testClass = new CoreLoggerAdvice(proceedingJoinPointLogger);

        testClass.setOverrideLogConfig(true);
        assertTrue(testClass.isDebugEnabled(logger));
        testClass.setOverrideLogConfig(false);
        assertEquals(logger.isDebugEnabled(), testClass.isDebugEnabled(logger));
    }

    @Test
    public void shouldBeInCorrectOrder() {
        assertEquals(LoggingConstants.LOGGING_ADVICE_ORDER, testClass.getOrder());
    }

    @Test
    public void shouldProceedLoggerDisabled() throws Throwable {

        testClass = new CoreLoggerAdvice(proceedingJoinPointLogger) {
            @Override
            protected boolean isDebugEnabled(final Logger targetObjectLogger) {
                return false;
            }
        };

        final Object pjpResult = "Expected result";

        when(pjp.getTarget()).thenReturn(target);
        when(pjp.proceed()).thenReturn(pjpResult);

        final Object result = testClass.doLogging(pjp);

        assertEquals(pjpResult, result);
    }

    @Test
    public void shouldProceedWithRealLogger() throws Throwable {

        testClass = new CoreLoggerAdvice(proceedingJoinPointLogger);

        final Object pjpResult = "Expected result";

        when(pjp.getTarget()).thenReturn(target);
        when(pjp.proceed()).thenReturn(pjpResult);

        final Object result = testClass.doLogging(pjp);

        assertEquals(pjpResult, result);
    }

    @Test
    public void shouldLogAndSwallowExceptions() throws Throwable {

        final Object pjpResult = "Expected result";

        when(pjp.getTarget()).thenReturn(target);
        when(pjp.proceed()).thenReturn(pjpResult);

        doThrow(new RuntimeException("")).when(proceedingJoinPointLogger)
                                         .logMethodEntry(notNull(Logger.class), eq(pjp));
        doThrow(new RuntimeException("")).when(proceedingJoinPointLogger)
                                         .logMethodExit(notNull(Logger.class),
                                                        eq(pjp),
                                                        eq(pjpResult));

        final Object result = testClass.doLogging(pjp);

        assertEquals(pjpResult, result);
    }

    @Test
    public void shouldLogEntryExit() throws Throwable {

        final Object pjpResult = "Expected result";

        when(pjp.getTarget()).thenReturn(target);
        when(pjp.proceed()).thenReturn(pjpResult);

        final Object result = testClass.doLogging(pjp);

        assertEquals(pjpResult, result);

        verify(proceedingJoinPointLogger).logMethodEntry(notNull(Logger.class), eq(pjp));
        verify(proceedingJoinPointLogger).logMethodExit(notNull(Logger.class),
                                                        eq(pjp),
                                                        eq(pjpResult));
    }

    @Test
    public void shouldLogEntryWithException() throws Throwable {

        final RuntimeException exception = new RuntimeException("Test exception");

        when(pjp.getTarget()).thenReturn(target);
        when(pjp.proceed()).thenThrow(exception);

        try {
            testClass.doLogging(pjp);

            fail("Expected RuntimeException");

        } catch (final RuntimeException e) {

            // This is expected

            assertEquals(exception, e);
        } finally {

            verify(proceedingJoinPointLogger).logMethodEntry(notNull(Logger.class), eq(pjp));
            verify(proceedingJoinPointLogger).logException(notNull(Logger.class),
                                                           eq(pjp),
                                                           eq(exception));
        }
    }

    @Test
    public void shouldSwallowExceptionWhenLoggingException() throws Throwable {

        final RuntimeException exception = new RuntimeException("Test exception");

        when(pjp.getTarget()).thenReturn(target);
        when(pjp.proceed()).thenThrow(exception);

        doThrow(new RuntimeException("")).when(proceedingJoinPointLogger)
                                         .logException(notNull(Logger.class),
                                                       eq(pjp),
                                                       eq(exception));
        try {
            testClass.doLogging(pjp);

            fail("Expected RuntimeException");

        } catch (final RuntimeException e) {

            // This is expected

            assertEquals(exception, e);
        } finally {

            verify(proceedingJoinPointLogger).logMethodEntry(notNull(Logger.class), eq(pjp));
            verify(proceedingJoinPointLogger).logException(notNull(Logger.class),
                                                           eq(pjp),
                                                           eq(exception));
        }
    }

}
