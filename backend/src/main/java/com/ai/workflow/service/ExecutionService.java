package com.ai.workflow.service;

import com.ai.workflow.engine.WorkflowEngine;
import com.ai.workflow.entity.ExecutionRecord;
import com.ai.workflow.repository.ExecutionNodeResultRepository;
import com.ai.workflow.repository.ExecutionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final WorkflowEngine workflowEngine;
    private final ExecutionRecordRepository executionRecordRepository;
    private final ExecutionNodeResultRepository nodeResultRepository;

    public Object executeWorkflow(Long workflowId, String userInput) {
        return workflowEngine.execute(workflowId, userInput, false);
    }

    public Object debugWorkflow(Long workflowId, String userInput) {
        return workflowEngine.execute(workflowId, userInput, true);
    }

    public ExecutionRecord getExecution(Long executionId) {
        return executionRecordRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));
    }

    public List<ExecutionRecord> getExecutionHistory(Long workflowId) {
        return executionRecordRepository.findByWorkflowIdOrderByCreatedAtDesc(workflowId);
    }
}
