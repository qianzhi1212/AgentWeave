package com.ai.workflow.dto.response;

import com.ai.workflow.enums.ExecutionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionResponse {
    private Long executionId;
    private Long workflowId;
    private ExecutionStatus status;
    private String userInput;
    private String finalOutput;
    private String errorMessage;
    private Boolean isDebug;
    private List<NodeExecutionResult> nodeResults;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}
