package com.Kidari.server.common.response.exception;

public class FollowException extends DefaultException {
    public FollowException(ErrorCode statusCode) {
        super(statusCode);
    }
}
