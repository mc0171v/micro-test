package com.vennetics.microservices.common.core.httplog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * Implementation of a servlet output stream that keeps a copy of all data
 * written to it. The copy can be used for operational purposes such as
 * debugging logs.
 * <P>
 * Not recommended in production code for performance reasons.
 */
public class ServletOutputStreamCopier extends ServletOutputStream {

    private static final int BYTE_ARRAY_SIZE = 1024;
    private final OutputStream outputStream;
    private final ByteArrayOutputStream copy;

    public ServletOutputStreamCopier(final OutputStream outputStream) {
        this.outputStream = outputStream;
        copy = new ByteArrayOutputStream(BYTE_ARRAY_SIZE);
    }

    @Override
    public void write(final int b) throws IOException {
        outputStream.write(b);
        copy.write(b);
    }

    public byte[] getCopy() {
        return copy.toByteArray();
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(final WriteListener writeListener) {
        //do nothing
    }

}
