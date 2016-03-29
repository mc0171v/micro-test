package com.vennetics.microservices.common.core.logging.aop;

import static com.vennetics.microservices.common.core.logging.LoggingConstants.INDENT;
import static com.vennetics.microservices.common.core.logging.LoggingConstants.NEW_LINE;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class StandardPjpLoggerTest {

    private static final String TYPE_NAME = "Type Name";

    private static final String PARAM_NAME = "Param Name";

    private static final String PARAM_VALUE = "Value";

    private static final String SIGNATURE_NAME = "signatureName";

    private static final String MOCK_RETURN_VALUE = "returnValue";

    private static final Observable<String> MOCK_OBSERVABLE_RETURN_VALUE = Observable.just("returnValue");

    private static final RuntimeException TEST_EXCEPTION = new RuntimeException("Test exception");

    @Mock
    private ICoreParameterNameDiscoverer paramNameDiscoverer;

    @Mock
    private Logger logger;

    @Mock
    private Signature signature;

    @Mock
    private ProceedingJoinPoint pjp;

    private StandardPjpLogger testClass;

    @Before
    public void init() {
        testClass = new StandardPjpLogger(paramNameDiscoverer);
    }

    @Test
    public void shouldLogSigAndParamsOnEntry() {

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn(TYPE_NAME);

        when(signature.getName()).thenReturn(SIGNATURE_NAME);
        when(pjp.getArgs()).thenReturn(new String[] { PARAM_VALUE });
        when(paramNameDiscoverer.getParamNames(pjp)).thenReturn(new String[] { PARAM_NAME });

        testClass.logMethodEntry(logger, pjp);

        verify(logger).debug(eq("{} >>> {}{}{}{}"),
                             eq(NEW_LINE),
                             eq(TYPE_NAME + "."),
                             eq(SIGNATURE_NAME),
                             contains(PARAM_NAME + "=[" + PARAM_VALUE + "]"),
                             eq(NEW_LINE));
    }

    @Test
    public void shouldLogResultOnExit() {

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn(TYPE_NAME);

        when(signature.getName()).thenReturn(SIGNATURE_NAME);
        when(pjp.getArgs()).thenReturn(new String[] { PARAM_VALUE });
        when(paramNameDiscoverer.getParamNames(pjp)).thenReturn(new String[] { PARAM_NAME });

        testClass.logMethodExit(logger, pjp, MOCK_RETURN_VALUE);

        verify(logger).debug(eq("{} <<< {}{}{}{}result={}{}"),
                             eq(NEW_LINE),
                             eq(TYPE_NAME + "."),
                             eq(SIGNATURE_NAME),
                             eq(NEW_LINE),
                             eq(INDENT),
                             eq("<" + MOCK_RETURN_VALUE + ">"),
                             eq(NEW_LINE));
    }

    @Test
    public void shouldSubscribeAndLogObservableOnExit() {

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn(TYPE_NAME);

        when(signature.getName()).thenReturn(SIGNATURE_NAME);
        when(pjp.getArgs()).thenReturn(new String[] { PARAM_VALUE });
        when(paramNameDiscoverer.getParamNames(pjp)).thenReturn(new String[] { PARAM_NAME });

        testClass.logMethodExit(logger, pjp, MOCK_OBSERVABLE_RETURN_VALUE);

        verify(logger).debug(eq("{} <<< {}{}{}{}observableResult={}{}"),
                             eq(NEW_LINE),
                             eq(TYPE_NAME + "."),
                             eq(SIGNATURE_NAME),
                             eq(NEW_LINE),
                             eq(INDENT),
                             eq("<" + MOCK_RETURN_VALUE + ">"),
                             eq(NEW_LINE));
    }

    @Test
    public void shouldLogMessageOnException() {

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn(TYPE_NAME);

        when(signature.getName()).thenReturn(SIGNATURE_NAME);
        when(pjp.getArgs()).thenReturn(new String[] { PARAM_VALUE });
        when(paramNameDiscoverer.getParamNames(pjp)).thenReturn(new String[] { PARAM_NAME });

        testClass.logException(logger, pjp, TEST_EXCEPTION);

        verify(logger).debug(eq("{} <<< Exception in method: {}{} Error Message: {}{}"),
                             eq(NEW_LINE),
                             eq(TYPE_NAME + "."),
                             eq(SIGNATURE_NAME),
                             eq(TEST_EXCEPTION.getMessage()),
                             eq(NEW_LINE));
    }

    @Test
    public void shouldLogBlankException() {

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn(TYPE_NAME);

        when(signature.getName()).thenReturn(SIGNATURE_NAME);
        when(pjp.getArgs()).thenReturn(new String[] { PARAM_VALUE });
        when(paramNameDiscoverer.getParamNames(pjp)).thenReturn(new String[] { PARAM_NAME });

        testClass.logException(logger, pjp, new RuntimeException());

        verify(logger).debug(eq("{} <<< Exception in method: {}{} Error Message: {}{}"),
                             eq(NEW_LINE),
                             eq(TYPE_NAME + "."),
                             eq(SIGNATURE_NAME),
                             eq("Blank message on exception type java.lang.RuntimeException"),
                             eq(NEW_LINE));
    }

}
