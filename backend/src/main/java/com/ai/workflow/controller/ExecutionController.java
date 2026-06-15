package com.ai.workflow.controller;

import com.ai.workflow.dto.request.WorkflowExecutionRequest;
import com.ai.workflow.dto.response.ExecutionResponse;
import com.ai.workflow.service.ExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/executions")
@RequiredArgsConstructor
public class ExecutionController {

    private final ExecutionService executionService;

    @PostMapping
    public ResponseEntity<ExecutionResponse> execute(@Valid @RequestBody WorkflowExecutionRequest request) {
        ExecutionResponse response = (ExecutionResponse) executionService.executeWorkflow(
                request.getWorkflowId(), request.getUserInput());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/debug")
    public ResponseEntity<ExecutionResponse> debug(@Valid @RequestBody WorkflowExecutionRequest request) {
        ExecutionResponse response = (ExecutionResponse) executionService.debugWorkflow(
                request.getWorkflowId(), request.getUserInput());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{executionId}")
    public ResponseEntity<ExecutionResponse> get(@PathVariable Long executionId) {
        // Simplified - return execution record as response
        var record = executionService.getExecution(executionId);
        ExecutionResponse response = ExecutionResponse.builder()
                .executionId(record.getId())
                .workflowId(record.getWorkflow().getId())
                .status(record.getStatus())
                .userInput(record.getUserInput())
                .finalOutput(record.getFinalOutput())
                .errorMessage(record.getErrorMessage())
                .isDebug(record.getIsDebug())
                .startedAt(record.getStartedAt())
                .finishedAt(record.getFinishedAt())
                .build();
        return ResponseEntity.ok(response);
    }
}
