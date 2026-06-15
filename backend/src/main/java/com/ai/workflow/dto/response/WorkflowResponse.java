package com.ai.workflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Integer version;
    private List<NodeResponse> nodes;
    private List<EdgeResponse> edges;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
