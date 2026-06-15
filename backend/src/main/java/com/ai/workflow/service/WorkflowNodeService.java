package com.ai.workflow.service;

import com.ai.workflow.entity.LlmProvider;
import com.ai.workflow.entity.Workflow;
import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.enums.NodeType;
import com.ai.workflow.exception.WorkflowNotFoundException;
import com.ai.workflow.repository.LlmProviderRepository;
import com.ai.workflow.repository.WorkflowEdgeRepository;
import com.ai.workflow.repository.WorkflowNodeRepository;
import com.ai.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowNodeService {

    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowEdgeRepository edgeRepository;
    private final WorkflowRepository workflowRepository;
    private final LlmProviderRepository llmProviderRepository;

    @Transactional
    public WorkflowNode addNode(Long workflowId, String nodeKey, String name, NodeType nodeType,
                                String config, Long llmProviderId, Integer positionX, Integer positionY) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found: " + workflowId));

        WorkflowNode node = WorkflowNode.builder()
                .workflow(workflow)
                .nodeKey(nodeKey)
                .name(name)
                .nodeType(nodeType)
                .config(config)
                .positionX(positionX)
                .positionY(positionY)
                .build();

        // Set LLM provider if specified
        if (llmProviderId != null) {
            LlmProvider provider = llmProviderRepository.findById(llmProviderId).orElse(null);
            node.setLlmProvider(provider);
        }

        return nodeRepository.save(node);
    }

    @Transactional
    public WorkflowNode updateNode(Long nodeId, String name, String config,
                                   Long llmProviderId, Integer positionX, Integer positionY) {
        WorkflowNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new WorkflowNotFoundException("Node not found: " + nodeId));
        node.setName(name);
        node.setConfig(config);
        node.setPositionX(positionX);
        node.setPositionY(positionY);
        if (llmProviderId != null) {
            LlmProvider provider = llmProviderRepository.findById(llmProviderId).orElse(null);
            node.setLlmProvider(provider);
        }
        return nodeRepository.save(node);
    }

    @Transactional
    public void deleteNode(Long nodeId) {
        edgeRepository.deleteBySourceNodeId(nodeId);
        edgeRepository.deleteByTargetNodeId(nodeId);
        nodeRepository.deleteById(nodeId);
    }

    public List<WorkflowNode> getNodesByWorkflowId(Long workflowId) {
        return nodeRepository.findByWorkflowId(workflowId);
    }
}
