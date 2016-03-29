package com.vennetics.microservices.common.not.core.logging;

/**
 * For test purposes in logging AOP only. Deliberabtely not in core.logging
 * package
 */
public interface ITestEcho {

    String echo(String testParam);

}
