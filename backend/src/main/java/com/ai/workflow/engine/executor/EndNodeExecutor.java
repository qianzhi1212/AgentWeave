package com.ai.workflow.engine.executor;

import com.ai.workflow.engine.ExecutionContext;
import com.ai.workflow.engine.NodeExecutor;
import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.enums.NodeType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EndNodeExecutor implements NodeExecutor {

    @Override
    public NodeType supportedType() {
        return NodeType.END;
    }

    @Override
    public Object execute(WorkflowNode node, ExecutionContext context) {
        // End node collects the last upstream output as final result
        Map<String, Object> output = new HashMap<>();
        Object lastOutput = context.getLastNodeOutput();
        if (lastOutput instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapOutput = (Map<String, Object>) lastOutput;
            output.putAll(mapOutput);
        } else if (lastOutput != null) {
            output.put("result", lastOutput);
        }
        context.setNodeOutput(node.getNodeKey(), output);
        return output;
    }
}
