package com.serverless.exceptions;

import com.serverless.service.implementations.ParkingServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpException extends RuntimeException {
    private static final Logger LOG = LogManager.getLogger(HttpException.class);

    public HttpException(Throwable cause) {
        super(cause);
        Throwable rootException = getRootException(cause);
        LOG.info("Exception: {}, Message: {}", rootException.getClass().toString(), rootException.getMessage());
    }

    private Throwable getRootException(Throwable e) {
        Throwable cause = null;
        Throwable result = e;
        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }
}
