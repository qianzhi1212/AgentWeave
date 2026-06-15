package com.ai.workflow.repository;

import com.ai.workflow.entity.ExecutionNodeResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutionNodeResultRepository extends JpaRepository<ExecutionNodeResult, Long> {
    List<ExecutionNodeResult> findByExecutionId(Long executionId);
}
