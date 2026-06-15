package com.ai.workflow.repository;

import com.ai.workflow.entity.ExecutionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutionRecordRepository extends JpaRepository<ExecutionRecord, Long> {
    List<ExecutionRecord> findByWorkflowIdOrderByCreatedAtDesc(Long workflowId);
}
