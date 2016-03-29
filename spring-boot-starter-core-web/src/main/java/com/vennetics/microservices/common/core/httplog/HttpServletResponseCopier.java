package com.vennetics.microservices.common.core.httplog;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP servlet response wrapper that keeps a copy of the response for logging
 * purposes.
 */
public class HttpServletResponseCopier extends HttpServletResponseWrapper {

    protected static final String FAILED_TO_BUFFER = "<FAILED TO BUFFER>";

    private static final Logger logger = LoggerFactory.getLogger(HttpServletResponseCopier.class.getName());

    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private ServletOutputStreamCopier copier;

    private static final int HTTP_STATUS_REDIRECT_FOUND = 302;

    public HttpServletResponseCopier(final HttpServletResponse response) {
        super(response);
    }

    // Override to set the status to 302 as spring class doesn't do this
    @Override
    public void sendRedirect(final String location) throws IOException {
        super.setStatus(HTTP_STATUS_REDIRECT_FOUND);
        super.sendRedirect(location);
    }

    @Override
    public void sendError(final int sc) throws IOException {
        super.setStatus(sc);
        super.sendError(sc);
    }

    @Override
    public void sendError(final int sc, final String msg) throws IOException {
        super.setStatus(sc);
        super.sendError(sc, msg);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = getResponse().getOutputStream();
            copier = getServletOutputStreamCopier();
        }

        return copier;
    }

    private ServletOutputStreamCopier getServletOutputStreamCopier() {
        return new ServletOutputStreamCopier(outputStream);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
            writer = new PrintWriter(new OutputStreamWriter(copier,
                                                            getResponse().getCharacterEncoding()),
                                     true);
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            copier.flush();
        }
    }

    /**
     * Get the maintained copy of the servlet response.
     *
     * @return the copy
     */
    public byte[] getCopy() {
        if (copier != null) {
            return copier.getCopy();
        }
        return new byte[0];
    }

    /**
     * @return the body as a string.
     */
    public String getResponseBody(final String encoding) {
        try {
            return new String(getCopy(), encoding != null ? encoding : "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            logger.warn("{} encoding failure in request body", encoding, e);
            return FAILED_TO_BUFFER;
        }
    }

}
