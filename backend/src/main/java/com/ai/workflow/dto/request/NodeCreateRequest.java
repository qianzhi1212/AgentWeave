package com.ai.workflow.dto.request;

import com.ai.workflow.enums.NodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeCreateRequest {
    @NotBlank
    private String nodeKey;
    @NotBlank
    private String name;
    @NotNull
    private NodeType nodeType;
    private String config;
    private Long llmProviderId;
    private Integer positionX;
    private Integer positionY;
}
