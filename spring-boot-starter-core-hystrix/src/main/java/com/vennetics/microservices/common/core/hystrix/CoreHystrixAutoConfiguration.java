package com.vennetics.microservices.common.core.hystrix;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configuration class. Will autowire this starter in a spring boot
 * environment.
 */
@Configuration
@ComponentScan("com.vennetics.microservices.common.core.hystrix")
@ConditionalOnProperty(
                prefix = "com.vennetics.microservices.common.core.hystrix",
                name = "auto",
                havingValue = "true",
                matchIfMissing = true)
@SuppressWarnings({ "squid:S2094" })
public class CoreHystrixAutoConfiguration {

}
