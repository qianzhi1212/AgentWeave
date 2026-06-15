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

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowEdgeRepository edgeRepository;

    @Transactional
    public Workflow createWorkflow(String name, String description) {
        Workflow workflow = Workflow.builder()
                .name(name)
                .description(description)
                .build();
        return workflowRepository.save(workflow);
    }

    public Workflow getWorkflow(Long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found: " + id));
    }

    public List<Workflow> listWorkflows() {
        return workflowRepository.findAll();
    }

    @Transactional
    public Workflow updateWorkflow(Long id, String name, String description) {
        Workflow workflow = getWorkflow(id);
        workflow.setName(name);
        workflow.setDescription(description);
        return workflowRepository.save(workflow);
    }

    @Transactional
    public void deleteWorkflow(Long id) {
        edgeRepository.deleteByWorkflowId(id);
        nodeRepository.deleteByWorkflowId(id);
        workflowRepository.deleteById(id);
    }

    public List<WorkflowNode> getNodes(Long workflowId) {
        return nodeRepository.findByWorkflowId(workflowId);
    }

    public List<WorkflowEdge> getEdges(Long workflowId) {
        return edgeRepository.findByWorkflowId(workflowId);
    }
}
