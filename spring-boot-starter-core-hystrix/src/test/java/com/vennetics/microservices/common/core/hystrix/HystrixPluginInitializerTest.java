package com.vennetics.microservices.common.core.hystrix;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.netflix.hystrix.strategy.HystrixPlugins;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HystrixPluginInitializer.class })
public class HystrixPluginInitializerTest {


    @Test
    public void shouldRegisterConcurrencyStrategy() {

        assertTrue(HystrixPlugins.getInstance()
                                 .getConcurrencyStrategy() instanceof CoreHystrixConcurrencyStrategy);
    }

    @Test
    public void shouldBeAbleToCreateAndDestroy() {
        final HystrixPluginInitializer testClass = new HystrixPluginInitializer();

        // Calling destroy first as spring wiring will have initialsed as per
        // above test.
        testClass.destroy();
        testClass.init();
    }

    @Test
    public void shouldAllowInitToBeRecalled() {
        final HystrixPluginInitializer testClass = new HystrixPluginInitializer();

        // Since spring-runner will have initialised
        // Calling again should reset hystrix and re-initialise
        testClass.init();
    }

}
