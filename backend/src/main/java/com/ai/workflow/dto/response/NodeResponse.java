package com.ai.workflow.dto.response;

import com.ai.workflow.enums.NodeType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeResponse {
    private Long id;
    private String nodeKey;
    private String name;
    private NodeType nodeType;
    private String config;
    private Long llmProviderId;
    private Integer positionX;
    private Integer positionY;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
