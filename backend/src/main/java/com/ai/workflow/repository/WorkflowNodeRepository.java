package com.ai.workflow.repository;

import com.ai.workflow.entity.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowNodeRepository extends JpaRepository<WorkflowNode, Long> {
    List<WorkflowNode> findByWorkflowId(Long workflowId);
    void deleteByWorkflowId(Long workflowId);
}
