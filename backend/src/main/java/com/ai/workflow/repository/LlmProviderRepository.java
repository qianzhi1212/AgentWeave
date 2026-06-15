package com.ai.workflow.repository;

import com.ai.workflow.entity.LlmProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LlmProviderRepository extends JpaRepository<LlmProvider, Long> {
    Optional<LlmProvider> findByIsDefaultTrue();
}
