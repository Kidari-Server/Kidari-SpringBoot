package com.Kidari.server.common.response.exception;

public class CommitException extends DefaultException {
    public CommitException(ErrorCode statusCode) {
        super(statusCode);
    }
}
