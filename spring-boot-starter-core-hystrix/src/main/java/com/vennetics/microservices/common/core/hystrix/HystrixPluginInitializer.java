package com.vennetics.microservices.common.core.hystrix;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;

/**
 * Manages the registration of custom or non-default netflix plugins.
 */
@Service
@Lazy(false)
@Scope("singleton")
public class HystrixPluginInitializer {

    /**
     * Timeout when resetting hystrix
     */
    private static final int RESET_TIMEOUT = 10;

    private static final Logger logger = LoggerFactory.getLogger(HystrixPluginInitializer.class);

    @PostConstruct
    public synchronized void init() {

        logger.info("Initialising Netflix plugins");

        // Call this before initialising for test safety as it uses statics
        resetHystrix();

        registerHystrixConcurrencyStrategy();

        logger.info("Netflix plugins initialised");
    }

    @PreDestroy
    public synchronized void destroy() {
        logger.info("Destroying Netflix PluginManager");
        resetHystrix();
    }

    private static synchronized void resetHystrix() {

        logger.info("Resetting Hystrix");
        Hystrix.reset(RESET_TIMEOUT, TimeUnit.SECONDS);
    }

    private void registerHystrixConcurrencyStrategy() {

        // This can throw illegal state meaning bean won'y get registered if
        // already registered.
        // Since this is a serious error don't want to catch and delete
        HystrixPlugins.getInstance()
                      .registerConcurrencyStrategy(new CoreHystrixConcurrencyStrategy());

        logger.info("Using Hystrix concurrency strategy - {}",
                    HystrixPlugins.getInstance().getConcurrencyStrategy().getClass());
    }
}
