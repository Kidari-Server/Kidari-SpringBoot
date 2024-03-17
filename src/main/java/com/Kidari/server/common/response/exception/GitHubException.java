package com.Kidari.server.common.response.exception;

public class GitHubException extends DefaultException {
    public GitHubException(ErrorCode statusCode) {
        super(statusCode);
    }
}
