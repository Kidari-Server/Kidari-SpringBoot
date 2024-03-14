package com.Kidari.server.common.response.exception;

public class UnivException extends DefaultException {
    public UnivException(ErrorCode statusCode) {
        super(statusCode);
    }
}
