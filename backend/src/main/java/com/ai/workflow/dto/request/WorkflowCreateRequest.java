package com.ai.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowCreateRequest {
    @NotBlank
    private String name;
    private String description;
}
