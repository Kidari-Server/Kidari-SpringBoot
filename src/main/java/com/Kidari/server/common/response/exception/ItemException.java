package com.Kidari.server.common.response.exception;

public class ItemException extends DefaultException {
    public ItemException(ErrorCode statusCode) {
        super(statusCode);
    }
}
