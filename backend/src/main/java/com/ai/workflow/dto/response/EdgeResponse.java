package com.ai.workflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdgeResponse {
    private Long id;
    private Long sourceNodeId;
    private Long targetNodeId;
    private String sourcePort;
    private String targetPort;
    private LocalDateTime createdAt;
}
