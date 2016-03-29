package com.vennetics.microservices.common.core.logging.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vennetics.microservices.common.core.logging.LoggingConfig;
import com.vennetics.microservices.common.not.core.logging.ITestEcho;
import com.vennetics.microservices.common.not.core.logging.TestEcho;

@Configuration
@Import(LoggingConfig.class)
public class LoggerAopTestConfiguration {

    @Bean
    public ITestEcho testEcho() {
        return new TestEcho();
    }
}
