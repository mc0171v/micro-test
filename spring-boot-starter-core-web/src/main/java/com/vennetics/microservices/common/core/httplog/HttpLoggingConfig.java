package com.vennetics.microservices.common.core.httplog;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@ConditionalOnProperty(
                       prefix = "com.vennetics.microservices.common.core.httplog",
                       name = "auto",
                       havingValue = "true",
                       matchIfMissing = true)
public class HttpLoggingConfig {
    
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        HttpLoggingFilter crlf = new HttpLoggingFilter();
        crlf.setIncludeClientInfo(true);
        crlf.setIncludeQueryString(true);
        crlf.setIncludePayload(false);
        crlf.setBeforeMessagePrefix("");
        crlf.setBeforeMessageSuffix(";");
        crlf.setAfterMessagePrefix("");
        crlf.setAfterMessageSuffix(";");
        
        return crlf;
    }
}
