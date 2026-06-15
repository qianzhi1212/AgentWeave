package com.ai.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowExecutionRequest {
    @NotNull
    private Long workflowId;
    @NotBlank
    private String userInput;
}
