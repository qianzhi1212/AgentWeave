package com.ai.workflow.controller;

import com.ai.workflow.dto.request.WorkflowCreateRequest;
import com.ai.workflow.dto.request.WorkflowUpdateRequest;
import com.ai.workflow.dto.response.EdgeResponse;
import com.ai.workflow.dto.response.NodeResponse;
import com.ai.workflow.dto.response.WorkflowResponse;
import com.ai.workflow.entity.Workflow;
import com.ai.workflow.entity.WorkflowEdge;
import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<WorkflowResponse> create(@Valid @RequestBody WorkflowCreateRequest request) {
        Workflow workflow = workflowService.createWorkflow(request.getName(), request.getDescription());
        return ResponseEntity.ok(toResponse(workflow));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowResponse> get(@PathVariable Long id) {
        Workflow workflow = workflowService.getWorkflow(id);
        List<WorkflowNode> nodes = workflowService.getNodes(id);
        List<WorkflowEdge> edges = workflowService.getEdges(id);
        return ResponseEntity.ok(toResponse(workflow, nodes, edges));
    }

    @GetMapping
    public ResponseEntity<List<WorkflowResponse>> list() {
        List<Workflow> workflows = workflowService.listWorkflows();
        List<WorkflowResponse> responses = workflows.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody WorkflowUpdateRequest request) {
        Workflow workflow = workflowService.updateWorkflow(id, request.getName(), request.getDescription());
        return ResponseEntity.ok(toResponse(workflow));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }

    private WorkflowResponse toResponse(Workflow workflow) {
        return WorkflowResponse.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .description(workflow.getDescription())
                .status(workflow.getStatus().name())
                .version(workflow.getVersion())
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();
    }

    private WorkflowResponse toResponse(Workflow workflow, List<WorkflowNode> nodes, List<WorkflowEdge> edges) {
        WorkflowResponse response = toResponse(workflow);
        response.setNodes(nodes.stream().map(n -> NodeResponse.builder()
                .id(n.getId())
                .nodeKey(n.getNodeKey())
                .name(n.getName())
                .nodeType(n.getNodeType())
                .config(n.getConfig())
                .llmProviderId(n.getLlmProvider() != null ? n.getLlmProvider().getId() : null)
                .positionX(n.getPositionX())
                .positionY(n.getPositionY())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build()).collect(Collectors.toList()));
        response.setEdges(edges.stream().map(e -> EdgeResponse.builder()
                .id(e.getId())
                .sourceNodeId(e.getSourceNode().getId())
                .targetNodeId(e.getTargetNode().getId())
                .sourcePort(e.getSourcePort())
                .targetPort(e.getTargetPort())
                .createdAt(e.getCreatedAt())
                .build()).collect(Collectors.toList()));
        return response;
    }
}
