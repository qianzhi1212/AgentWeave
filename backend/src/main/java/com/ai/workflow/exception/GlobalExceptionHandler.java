package com.ai.workflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleWorkflowNotFound(WorkflowNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "WORKFLOW_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(CyclicDependencyException.class)
    public ResponseEntity<Map<String, Object>> handleCyclic(CyclicDependencyException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "CYCLIC_DEPENDENCY", ex.getMessage());
    }

    @ExceptionHandler(NodeExecutionException.class)
    public ResponseEntity<Map<String, Object>> handleNodeExecution(NodeExecutionException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "NODE_EXECUTION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("code", code);
        body.put("message", message);
        body.put("status", status.value());
        return ResponseEntity.status(status).body(body);
    }
}
