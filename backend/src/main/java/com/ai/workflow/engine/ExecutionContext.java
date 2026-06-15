package com.ai.workflow.engine;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {

    private final Long executionId;
    private final Long workflowId;
    private final String userInput;
    private final Map<String, Object> nodeOutputs;
    private final boolean debugMode;

    public ExecutionContext(Long executionId, Long workflowId, String userInput, boolean debugMode) {
        this.executionId = executionId;
        this.workflowId = workflowId;
        this.userInput = userInput;
        this.nodeOutputs = new HashMap<>();
        this.debugMode = debugMode;
    }

    public Long getExecutionId() { return executionId; }
    public Long getWorkflowId() { return workflowId; }
    public String getUserInput() { return userInput; }
    public boolean isDebugMode() { return debugMode; }

    public Object getNodeOutput(String nodeKey) {
        return nodeOutputs.get(nodeKey);
    }

    public Map<String, Object> getAllNodeOutputs() {
        return new HashMap<>(nodeOutputs);
    }

    public void setNodeOutput(String nodeKey, Object output) {
        nodeOutputs.put(nodeKey, output);
    }

    /** Get the last node output (for End node) */
    public Object getLastNodeOutput() {
        if (nodeOutputs.isEmpty()) return null;
        String lastKey = null;
        for (String key : nodeOutputs.keySet()) {
            lastKey = key;
        }
        return nodeOutputs.get(lastKey);
    }
}
