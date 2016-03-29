package com.vennetics.microservices.common.core.logging.aop;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.vennetics.microservices.common.not.core.logging.ITestEcho;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { LoggerAopTestConfiguration.class })
public class CoreLoggerAdviceSpringTest {

    private static final Logger logger = LoggerFactory.getLogger(CoreLoggerAdviceSpringTest.class);

    private IProceedingJoinPointLogger mockPjpLogger;

    @Autowired
    private IProceedingJoinPointLogger realPjpLogger;

    @Autowired
    private ITestEcho testInterface;

    @Autowired
    private CoreLoggerAdvice coreLoggerAdvice;

    @Before
    public void init() {
        mockPjpLogger = mock(IProceedingJoinPointLogger.class);

        if (!logger.isDebugEnabled()) {
            logger.warn("Expected debug to be enabled for this test. Is the logging configuration correct");
        }
    }

    @Test
    public void shouldLogToPjpLogger() throws Throwable {

        // Use a mock to capture logging
        ReflectionTestUtils.setField(coreLoggerAdvice, "proceedingJoinPointLogger", mockPjpLogger);
        ReflectionTestUtils.setField(coreLoggerAdvice, "overrideLogConfig", true);

        logger.debug("Invoking advised class: {} ", testInterface);

        final String result = testInterface.echo("testParam");

        assertEquals("Echo:" + "testParam", result);

        logger.debug("Got result:{}", result);

        // Verify AOP calls
        verify(mockPjpLogger).logMethodEntry(notNull(Logger.class),
                                             notNull(ProceedingJoinPoint.class));
        verify(mockPjpLogger).logMethodExit(notNull(Logger.class),
                                            notNull(ProceedingJoinPoint.class),
                                            eq("Echo:" + "testParam"));
    }

    @Test
    public void shouldRunWithoutErrorsWithRealAspectLogger() throws Throwable {

        // Use real advice to ensure no errors
        ReflectionTestUtils.setField(coreLoggerAdvice, "proceedingJoinPointLogger", realPjpLogger);

        logger.debug("Invoking advised class: {} ", testInterface);

        final String result = testInterface.echo("testParam");

        assertEquals("Echo:" + "testParam", result);

        logger.debug("Got result:{}", result);
    }

}
