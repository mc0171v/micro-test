package com.vennetics.microservices.common.core.logging.aop;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterNameDiscoverer;

@RunWith(MockitoJUnitRunner.class)
public class CoreParameterNameDiscovererTest {

    private static final String[] NAMES = { "Name1", "Name2" };

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private ParameterNameDiscoverer parameterNameDiscoverer;

    @Mock
    private MethodSignature signature;

    private Method method;

    private CoreParameterNameDiscoverer testClass;

    @Before
    public void init() {

        testClass = new CoreParameterNameDiscoverer(parameterNameDiscoverer);
    }

    @Test
    public void shouldUseSpringDefaultsWhenNotAnInterface() throws Exception {
        method = Object.class.getMethod("equals", Object.class);

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(parameterNameDiscoverer.getParameterNames(method)).thenReturn(NAMES);

        final String[] result = testClass.getParamNames(pjp);

        assertTrue(Arrays.equals(NAMES, result));

    }

}
