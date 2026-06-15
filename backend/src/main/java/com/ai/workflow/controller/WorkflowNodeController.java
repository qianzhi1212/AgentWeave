package com.ai.workflow.controller;

import com.ai.workflow.dto.request.NodeCreateRequest;
import com.ai.workflow.dto.request.NodeUpdateRequest;
import com.ai.workflow.dto.response.NodeResponse;
import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.service.WorkflowNodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workflows/{workflowId}/nodes")
@RequiredArgsConstructor
public class WorkflowNodeController {

    private final WorkflowNodeService nodeService;

    @PostMapping
    public ResponseEntity<NodeResponse> add(@PathVariable Long workflowId,
                                             @Valid @RequestBody NodeCreateRequest request) {
        WorkflowNode node = nodeService.addNode(
                workflowId, request.getNodeKey(), request.getName(), request.getNodeType(),
                request.getConfig(), request.getLlmProviderId(),
                request.getPositionX(), request.getPositionY());
        return ResponseEntity.ok(toResponse(node));
    }

    @PutMapping("/{nodeId}")
    public ResponseEntity<NodeResponse> update(@PathVariable Long workflowId,
                                                @PathVariable Long nodeId,
                                                @RequestBody NodeUpdateRequest request) {
        WorkflowNode node = nodeService.updateNode(
                nodeId, request.getName(), request.getConfig(),
                request.getLlmProviderId(), request.getPositionX(), request.getPositionY());
        return ResponseEntity.ok(toResponse(node));
    }

    @DeleteMapping("/{nodeId}")
    public ResponseEntity<Void> delete(@PathVariable Long workflowId, @PathVariable Long nodeId) {
        nodeService.deleteNode(nodeId);
        return ResponseEntity.noContent().build();
    }

    private NodeResponse toResponse(WorkflowNode node) {
        return NodeResponse.builder()
                .id(node.getId())
                .nodeKey(node.getNodeKey())
                .name(node.getName())
                .nodeType(node.getNodeType())
                .config(node.getConfig())
                .positionX(node.getPositionX())
                .positionY(node.getPositionY())
                .createdAt(node.getCreatedAt())
                .updatedAt(node.getUpdatedAt())
                .build();
    }
}
