package com.vennetics.microservices.common.core.httplog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

public class HttpServletResponseCopierTest {

    @Mock
    private HttpServletResponse mockResponse;

    private MockHttpServletResponse springMockHttpServletResponse;
    
    @Mock
    private PrintWriter printWriter;

    @Mock
    private ServletOutputStream servletOutputStream;

    private HttpServletResponseCopier servletResponseCopier;
    
    @Mock
    private ServletOutputStreamCopier servletOutputStreamCopier;

    @Before
    public void setup() {
        mockResponse = mock(HttpServletResponse.class);

        servletOutputStream = mock(ServletOutputStream.class);

        springMockHttpServletResponse = new MockHttpServletResponse();

        servletResponseCopier = new HttpServletResponseCopier(springMockHttpServletResponse);
    }

    private void setUpWithMockitoMock() {
        servletResponseCopier = new HttpServletResponseCopier(mockResponse);
        
        servletOutputStreamCopier = mock(ServletOutputStreamCopier.class);
        
        printWriter = mock(PrintWriter.class);

        try {
            when(mockResponse.getOutputStream()).thenReturn(servletOutputStream);
            
            when(mockResponse.getCharacterEncoding()).thenReturn("UTF-8");
        } catch (IOException e1) {
            fail("Failed to create mock servlet output stream");
        }

    }

    @Test
    public void testRedirectErrorCodeSetTo302() {

        try {
            servletResponseCopier.sendRedirect("North");
        } catch (IOException e) {
            fail("Exception thrown sending redirect in test");
        }

        assertEquals(HttpStatus.FOUND.value(), servletResponseCopier.getStatus());
    }

    @Test
    public void testErrorIsSent() {
        try {
            servletResponseCopier.sendError(HttpStatus.FORBIDDEN.value());
        } catch (IOException e) {
            fail("Exception thrown sending forbidden error");
        }

        assertEquals(HttpStatus.FORBIDDEN.value(), servletResponseCopier.getStatus());
    }

    @Test
    public void testErrorIsSentWithMessage() {
        try {
            servletResponseCopier.sendError(HttpStatus.FORBIDDEN.value(), "message");
        } catch (IOException e) {
            fail("Exception thrown sending forbidden error");
        }

        assertEquals(HttpStatus.FORBIDDEN.value(), servletResponseCopier.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateExceptionThrownWhenOutputStreamIsNull() throws Exception {
        servletResponseCopier.getWriter();
        servletResponseCopier.getOutputStream();
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateExceptionThrownWhenWriterIsNull() throws Exception {
        servletResponseCopier.getOutputStream();
        servletResponseCopier.getWriter();
    }

    @Test
    public void testGetOutputStreamReturnsZeroLengthServletOutputStreamCopier() {

        setUpWithMockitoMock();

        ServletOutputStreamCopier copier = null;

        try {
            copier = (ServletOutputStreamCopier) servletResponseCopier.getOutputStream();

            assertTrue(copier.getCopy().length == 0);

        } catch (IOException e) {
            fail("IOException with creating ServletOutputStramCopier");
        } finally {
            try {
                if (copier != null) {
                    copier.close();
                }
            } catch (IOException e) {
                fail("Caught IOException when closing copier");
            }
        }
    }

    @Test
    public void testGetWriterReturnsMockPrintWriter() {
        setUpWithMockitoMock();

        PrintWriter writer = null;

        try {
            ReflectionTestUtils.setField(servletResponseCopier, "writer", printWriter);
            
            writer = servletResponseCopier.getWriter();
            
            assertEquals(printWriter, writer);

        } catch (IOException e) {
            fail("IOException with creating ServletOutputStramCopier");
        } finally {

            if (writer != null) {
                writer.close();
            }
        }
    }
    
    @Test
    public void writerBuffersAreFlushed() {
        
        setUpWithMockitoMock();
        
        try {
            servletResponseCopier.getWriter();
            
            ReflectionTestUtils.setField(servletResponseCopier, "writer", printWriter);
            
            servletResponseCopier.flushBuffer();
            
            verify(printWriter).flush();
        } catch (IOException e) {
            fail("Flush buffer test");
        }
    }

    @Test
    public void copierBuffersAreFlushed() {
        
        setUpWithMockitoMock();
        
        try {
            
            servletResponseCopier.getOutputStream();
            
            ReflectionTestUtils.setField(servletResponseCopier, "copier", servletOutputStreamCopier);
            
            servletResponseCopier.flushBuffer();
            
            verify(servletOutputStreamCopier).flush();
        } catch (IOException e) {
            fail("Flush buffer test");
        }
    }
    
    @Test
    public void testGetCopyReturnsEmptyByteArrayWhenCopierIsNull() {
        assertTrue(servletResponseCopier.getCopy().length == 0);
    }
    
    @Test
    public void testGetCopyReturnsCopierByteArray() {
        
        setUpWithMockitoMock();
        
        ReflectionTestUtils.setField(servletResponseCopier, "copier", servletOutputStreamCopier);
        
        byte[] expected = {10};
        when(servletOutputStreamCopier.getCopy()).thenReturn(expected);
        
        assertEquals(expected, servletResponseCopier.getCopy());
    }
    
    @Test
    public void testGetResponseBodyReturnsEmptyString() {
        assertEquals("", servletResponseCopier.getResponseBody("UTF-8"));
    }
    
    @Test
    public void testGetResponseWhenEncodingIsNull() {
        assertEquals("", servletResponseCopier.getResponseBody(null));
    }
    
    @Test
    public void testGetResponseBodyReturnsErrorStringWhenEncodingNotUTF8() {
        assertEquals(HttpServletResponseCopier.FAILED_TO_BUFFER, servletResponseCopier.getResponseBody("UTF-9"));
    }
}
