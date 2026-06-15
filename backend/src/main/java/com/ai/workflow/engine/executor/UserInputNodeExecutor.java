package com.ai.workflow.engine.executor;

import com.ai.workflow.engine.ExecutionContext;
import com.ai.workflow.engine.NodeExecutor;
import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.enums.NodeType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserInputNodeExecutor implements NodeExecutor {

    @Override
    public NodeType supportedType() {
        return NodeType.USER_INPUT;
    }

    @Override
    public Object execute(WorkflowNode node, ExecutionContext context) {
        // User input node simply passes the user input to downstream nodes
        Map<String, Object> output = new HashMap<>();
        output.put("text", context.getUserInput());
        context.setNodeOutput(node.getNodeKey(), output);
        return output;
    }
}
