package com.ai.workflow.controller;

import com.ai.workflow.dto.request.EdgeCreateRequest;
import com.ai.workflow.dto.response.EdgeResponse;
import com.ai.workflow.entity.WorkflowEdge;
import com.ai.workflow.service.WorkflowEdgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workflows/{workflowId}/edges")
@RequiredArgsConstructor
public class WorkflowEdgeController {

    private final WorkflowEdgeService edgeService;

    @PostMapping
    public ResponseEntity<EdgeResponse> add(@PathVariable Long workflowId,
                                             @Valid @RequestBody EdgeCreateRequest request) {
        WorkflowEdge edge = edgeService.addEdge(
                workflowId, request.getSourceNodeId(), request.getTargetNodeId(),
                request.getSourcePort(), request.getTargetPort());
        return ResponseEntity.ok(toResponse(edge));
    }

    @DeleteMapping("/{edgeId}")
    public ResponseEntity<Void> delete(@PathVariable Long workflowId, @PathVariable Long edgeId) {
        edgeService.deleteEdge(edgeId);
        return ResponseEntity.noContent().build();
    }

    private EdgeResponse toResponse(WorkflowEdge edge) {
        return EdgeResponse.builder()
                .id(edge.getId())
                .sourceNodeId(edge.getSourceNode().getId())
                .targetNodeId(edge.getTargetNode().getId())
                .sourcePort(edge.getSourcePort())
                .targetPort(edge.getTargetPort())
                .createdAt(edge.getCreatedAt())
                .build();
    }
}
