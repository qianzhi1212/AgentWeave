package com.ai.workflow.exception;

public class NodeExecutionException extends RuntimeException {
    public NodeExecutionException(String message) {
        super(message);
    }
    public NodeExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
