package com.ai.workflow.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeUpdateRequest {
    private String name;
    private String config;
    private Long llmProviderId;
    private Integer positionX;
    private Integer positionY;
}
