package com.vennetics.microservices.common.core.hystrix;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CoreHystrixAutoConfiguration.class })
public class CoreHystrixAutoConfigurationTest {

    @Autowired
    private HystrixPluginInitializer initialiser;

    @Test
    public void shouldCreateInitialiserBean() {

        assertNotNull(initialiser);
    }

}
