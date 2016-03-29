package com.vennetics.microservices.common.core.httplog;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponseWrapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class HttpLoggingFilterTest {

    private HttpLoggingFilter filter;

    @Mock
    private MockHttpServletRequest httpServletRequest;

    @Mock
    private MockHttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        filter = new HttpLoggingFilter();
    }

    @Test
    public void testThreadLocalVariableIsRemovedWhenNoExceptionIsThrown() throws Exception,
                                                                          ServletException {
        try {
            filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        } catch (ServletException e) {
            fail("Servlet exception caught");
        } catch (IOException e) {
            fail("IO Exception caught");
        }

        Field field = ReflectionUtils.findField(HttpLoggingFilter.class,
                                                "threadLocalResponseWrapper");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        ThreadLocal<HttpServletResponseWrapper> thread = (ThreadLocal<HttpServletResponseWrapper>) ReflectionUtils.getField(field,
                                                                                                                            filter);

        assertTrue(thread.get() == null);
    }

    @Test
    public void testThreadLocalVariableIsRemovedWhenIOExceptionIsThrown() {

        HttpLoggingFilter spy = Mockito.spy(new HttpLoggingFilter());

        try {

            doThrow(IOException.class).when((OncePerRequestFilter) spy).doFilter(httpServletRequest,
                                                                                 httpServletResponse,
                                                                                 filterChain);

            spy.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }

        Field field = ReflectionUtils.findField(HttpLoggingFilter.class,
                                                "threadLocalResponseWrapper");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        ThreadLocal<HttpServletResponseWrapper> thread = (ThreadLocal<HttpServletResponseWrapper>) ReflectionUtils.getField(field,
                                                                                                                            spy);

        assertTrue(thread.get() == null);

    }

    @Test
    public void testThreadLocalVariableIsRemovedWhenServletExceptionIsThrown() {

        HttpLoggingFilter spy = Mockito.spy(new HttpLoggingFilter());

        try {

            doThrow(ServletException.class).when((OncePerRequestFilter) spy)
                                           .doFilter(httpServletRequest,
                                                     httpServletResponse,
                                                     filterChain);

            spy.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        } catch (Exception e) {
            assertTrue(e instanceof ServletException);
        }

        Field field = ReflectionUtils.findField(HttpLoggingFilter.class,
                                                "threadLocalResponseWrapper");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        ThreadLocal<HttpServletResponseWrapper> thread = (ThreadLocal<HttpServletResponseWrapper>) ReflectionUtils.getField(field,
                                                                                                                            spy);

        assertTrue(thread.get() == null);
    }

    @Test
    public void testThreadLocalIsCalledForLoggingStatement() {
        @SuppressWarnings("unchecked")
        ThreadLocal<HttpServletResponseWrapper> thread = mock(ThreadLocal.class);
        HttpServletResponseWrapper value = new HttpServletResponseWrapper(httpServletResponse);
        value.setStatus(0);
        when(thread.get()).thenReturn(value);

        Field field = ReflectionUtils.findField(HttpLoggingFilter.class,
                                                "threadLocalResponseWrapper");
        field.setAccessible(true);

        ReflectionUtils.setField(field, filter, thread);

        filter.afterRequest(httpServletRequest, "Test");

        verify(thread, times(2)).get();
    }
    
    @Test
    public void testThreadLocalIsNotCalledWhenEmpty() {
        @SuppressWarnings("unchecked")
        ThreadLocal<HttpServletResponseWrapper> thread = mock(ThreadLocal.class);
        
        when(thread.get()).thenReturn(null);

        Field field = ReflectionUtils.findField(HttpLoggingFilter.class,
                                                "threadLocalResponseWrapper");
        field.setAccessible(true);

        ReflectionUtils.setField(field, filter, thread);

        filter.afterRequest(httpServletRequest, "Test");

        verify(thread, times(1)).get();
    }

}
