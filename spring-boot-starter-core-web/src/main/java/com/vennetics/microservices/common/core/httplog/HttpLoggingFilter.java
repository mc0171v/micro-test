package com.vennetics.microservices.common.core.httplog;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

public class HttpLoggingFilter extends CommonsRequestLoggingFilter {

    @SuppressWarnings({"hiding" , "squid:S2387"})
    private static final Logger logger = LoggerFactory.getLogger(HttpLoggingFilter.class.getName());
    
    private ThreadLocal<HttpServletResponseWrapper> threadLocalResponseWrapper = new ThreadLocal<>();
    
    public HttpLoggingFilter() {
        super();
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        
        HttpServletResponseWrapper wrapper = new HttpServletResponseCopier(response);
        
        threadLocalResponseWrapper.set(wrapper);
        try {
            
            super.doFilterInternal(request, wrapper, filterChain);
            
        } finally {
            
            threadLocalResponseWrapper.remove();
            
        }
    }
    
    @Override
    protected void beforeRequest(final HttpServletRequest request, final String message) {
        // do nothing
    }

    @Override
    protected void afterRequest(final HttpServletRequest request, final String message) {
        String debugMessage = message;
        
        // this logs the request
        super.afterRequest(request, debugMessage);

        if (threadLocalResponseWrapper.get() != null) {
        
            debugMessage += "httpStatus=" + threadLocalResponseWrapper.get().getStatus();
        
        }
        
        // this logs the response with httpStatus code
        logger.debug(message);
    }

}
