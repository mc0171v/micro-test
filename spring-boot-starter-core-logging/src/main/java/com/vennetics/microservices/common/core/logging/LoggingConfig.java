package com.vennetics.microservices.common.core.logging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * Applies logging AOP. Can be disabled by setting
 * com.vennetics.microservices.common.core.logging=false.
 */
@Configuration
@ComponentScan("com.vennetics.microservices.common.core.logging")
@EnableAspectJAutoProxy
@ConditionalOnProperty(
                prefix = "com.vennetics.microservices.common.core.logging",
                name = "auto",
                havingValue = "true",
                matchIfMissing = true)
public class LoggingConfig {

    /**
     * @return a Spring parameter name discoverer for getting argument names.
     */
    @Bean
    public ParameterNameDiscoverer paramterNameDiscoverer() {
        return new LocalVariableTableParameterNameDiscoverer();
    }
}
