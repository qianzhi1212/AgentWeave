package com.ai.workflow.engine;

import com.ai.workflow.enums.NodeType;

public interface NodeExecutor {

    NodeType supportedType();

    Object execute(com.ai.workflow.entity.WorkflowNode node, ExecutionContext context);
}
