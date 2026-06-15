package com.ai.workflow.repository;

import com.ai.workflow.entity.WorkflowEdge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowEdgeRepository extends JpaRepository<WorkflowEdge, Long> {
    List<WorkflowEdge> findByWorkflowId(Long workflowId);
    void deleteByWorkflowId(Long workflowId);
    void deleteBySourceNodeId(Long sourceNodeId);
    void deleteByTargetNodeId(Long targetNodeId);
}
