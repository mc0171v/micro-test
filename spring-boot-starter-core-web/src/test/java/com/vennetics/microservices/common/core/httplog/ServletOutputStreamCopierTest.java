package com.vennetics.microservices.common.core.httplog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class ServletOutputStreamCopierTest {

    @Test
    public void testOutputStreamIsCopied() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       
        ServletOutputStreamCopier servletOutputStreamCopier = new ServletOutputStreamCopier(byteArrayOutputStream);
        try {
            servletOutputStreamCopier.write(10);
        } catch (IOException e) {
            fail("Exception caught writing bytes in test");
        } finally {
            try {
                servletOutputStreamCopier.close();
            } catch (IOException e) {
                fail("Exception caught closing servletOutputStreamCopier");
            }
        }
        
        byte[] expectedBytes = {10};
        assertArrayEquals(expectedBytes, servletOutputStreamCopier.getCopy());
    }
    
    @Test
    public void testServletOutPutStreamIsNotReady() {
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        ServletOutputStreamCopier servletOutputStreamCopier = new ServletOutputStreamCopier(byteArrayOutputStream);
        try {
            servletOutputStreamCopier.close();
        } catch (IOException e) {
            fail("Exception caught closing servletOutputStreamCopier");
        }
        
        assertFalse(servletOutputStreamCopier.isReady());
    }
}
