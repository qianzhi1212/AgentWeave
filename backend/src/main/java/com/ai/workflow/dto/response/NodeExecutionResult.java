package com.ai.workflow.dto.response;

import com.ai.workflow.enums.ExecutionStatus;
import com.ai.workflow.enums.NodeType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeExecutionResult {
    private String nodeKey;
    private NodeType nodeType;
    private ExecutionStatus status;
    private String inputData;
    private String outputData;
    private String errorMessage;
    private Integer tokenUsage;
    private Long durationMs;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}
