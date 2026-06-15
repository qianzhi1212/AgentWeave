package com.ai.workflow.config;

import com.ai.workflow.engine.NodeExecutor;
import com.ai.workflow.enums.NodeType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class EngineConfig {

    @Bean
    public Map<NodeType, NodeExecutor> executorMap(List<NodeExecutor> executors) {
        return executors.stream()
                .collect(Collectors.toMap(NodeExecutor::supportedType, e -> e));
    }
}
