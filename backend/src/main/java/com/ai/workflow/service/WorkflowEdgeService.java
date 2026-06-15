package com.ai.workflow.service;

import com.ai.workflow.entity.Workflow;
import com.ai.workflow.entity.WorkflowEdge;
import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.exception.WorkflowNotFoundException;
import com.ai.workflow.repository.WorkflowEdgeRepository;
import com.ai.workflow.repository.WorkflowNodeRepository;
import com.ai.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowEdgeService {

    private final WorkflowEdgeRepository edgeRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowRepository workflowRepository;

    @Transactional
    public WorkflowEdge addEdge(Long workflowId, Long sourceNodeId, Long targetNodeId,
                                String sourcePort, String targetPort) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found: " + workflowId));
        WorkflowNode sourceNode = nodeRepository.findById(sourceNodeId)
                .orElseThrow(() -> new WorkflowNotFoundException("Source node not found: " + sourceNodeId));
        WorkflowNode targetNode = nodeRepository.findById(targetNodeId)
                .orElseThrow(() -> new WorkflowNotFoundException("Target node not found: " + targetNodeId));

        WorkflowEdge edge = WorkflowEdge.builder()
                .workflow(workflow)
                .sourceNode(sourceNode)
                .targetNode(targetNode)
                .sourcePort(sourcePort)
                .targetPort(targetPort)
                .build();

        return edgeRepository.save(edge);
    }

    @Transactional
    public void deleteEdge(Long edgeId) {
        edgeRepository.deleteById(edgeId);
    }
}
