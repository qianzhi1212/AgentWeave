package com.ai.workflow.llm.exception;

public class LlmCallException extends RuntimeException {
    public LlmCallException(String message) {
        super(message);
    }
    public LlmCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
