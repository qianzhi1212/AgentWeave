package com.ai.workflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdgeCreateRequest {
    @NotNull
    private Long sourceNodeId;
    @NotNull
    private Long targetNodeId;
    private String sourcePort;
    private String targetPort;
}
