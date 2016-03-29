package com.vennetics.microservices.common.not.core.logging;

public class TestEcho implements ITestEcho {

    @Override
    public String echo(final String testParam) {

        return "Echo:" + testParam;

    }

}
