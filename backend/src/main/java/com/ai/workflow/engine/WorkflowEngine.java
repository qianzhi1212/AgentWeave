package com.ai.workflow.engine;

import com.ai.workflow.dto.response.ExecutionResponse;
import com.ai.workflow.dto.response.NodeExecutionResult;
import com.ai.workflow.entity.*;
import com.ai.workflow.enums.ExecutionStatus;
import com.ai.workflow.enums.NodeType;
import com.ai.workflow.exception.NodeExecutionException;
import com.ai.workflow.exception.WorkflowNotFoundException;
import com.ai.workflow.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowEngine {

    private final Map<NodeType, NodeExecutor> executorMap;
    private final WorkflowRepository workflowRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowEdgeRepository edgeRepository;
    private final ExecutionRecordRepository executionRecordRepository;
    private final ExecutionNodeResultRepository nodeResultRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ExecutionResponse execute(Long workflowId, String userInput, boolean debugMode) {
        // 1. Load workflow
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found: " + workflowId));

        List<WorkflowNode> nodes = nodeRepository.findByWorkflowId(workflowId);
        List<WorkflowEdge> edges = edgeRepository.findByWorkflowId(workflowId);

        // 2. Topological sort
        List<WorkflowNode> sortedNodes = TopologicalSorter.sort(nodes, edges);

        // 3. Create execution record
        ExecutionRecord record = ExecutionRecord.builder()
                .workflow(workflow)
                .status(ExecutionStatus.RUNNING)
                .userInput(userInput)
                .isDebug(debugMode)
                .startedAt(LocalDateTime.now())
                .build();
        record = executionRecordRepository.save(record);

        // 4. Create execution context
        ExecutionContext context = new ExecutionContext(record.getId(), workflowId, userInput, debugMode);

        List<NodeExecutionResult> nodeResults = new ArrayList<>();
        boolean hasError = false;

        // 5. Execute nodes in order
        for (WorkflowNode node : sortedNodes) {
            NodeExecutionResult nodeResult = NodeExecutionResult.builder()
                    .nodeKey(node.getNodeKey())
                    .nodeType(node.getNodeType())
                    .status(ExecutionStatus.RUNNING)
                    .startedAt(LocalDateTime.now())
                    .build();

            try {
                NodeExecutor executor = executorMap.get(node.getNodeType());
                if (executor == null) {
                    throw new NodeExecutionException("No executor for node type: " + node.getNodeType());
                }

                // Collect upstream input data
                Object upstreamData = collectUpstreamInput(node, edges, context);
                nodeResult.setInputData(objectMapper.writeValueAsString(upstreamData));

                // Execute
                Object output = executor.execute(node, context);
                nodeResult.setOutputData(objectMapper.writeValueAsString(output));
                nodeResult.setStatus(ExecutionStatus.SUCCESS);

            } catch (Exception e) {
                log.error("Node execution failed: {} - {}", node.getNodeKey(), e.getMessage());
                nodeResult.setStatus(ExecutionStatus.FAILED);
                nodeResult.setErrorMessage(e.getMessage());
                hasError = true;
                if (!debugMode) break; // Stop on error in non-debug mode
            }

            nodeResult.setFinishedAt(LocalDateTime.now());
            nodeResult.setDurationMs(java.time.Duration.between(
                    nodeResult.getStartedAt(), nodeResult.getFinishedAt()).toMillis());
            nodeResults.add(nodeResult);
        }

        // 6. Update execution record
        record.setFinishedAt(LocalDateTime.now());
        if (hasError) {
            record.setStatus(ExecutionStatus.FAILED);
            record.setErrorMessage("One or more nodes failed during execution");
        } else {
            record.setStatus(ExecutionStatus.SUCCESS);
            try {
                record.setFinalOutput(objectMapper.writeValueAsString(context.getLastNodeOutput()));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                record.setFinalOutput("{}");
            }
        }
        executionRecordRepository.save(record);

        // 7. Build response
        return ExecutionResponse.builder()
                .executionId(record.getId())
                .workflowId(workflowId)
                .status(record.getStatus())
                .userInput(userInput)
                .finalOutput(record.getFinalOutput())
                .errorMessage(record.getErrorMessage())
                .isDebug(debugMode)
                .nodeResults(debugMode ? nodeResults : null)
                .startedAt(record.getStartedAt())
                .finishedAt(record.getFinishedAt())
                .build();
    }

    private Object collectUpstreamInput(WorkflowNode node, List<WorkflowEdge> edges, ExecutionContext context) {
        Map<String, Object> inputs = new java.util.LinkedHashMap<>();
        for (WorkflowEdge edge : edges) {
            if (edge.getTargetNode().getId().equals(node.getId())) {
                String sourceKey = edge.getSourceNode().getNodeKey();
                Object output = context.getNodeOutput(sourceKey);
                if (output != null) {
                    inputs.put(sourceKey, output);
                }
            }
        }
        return inputs.isEmpty() ? context.getUserInput() : inputs;
    }
}
