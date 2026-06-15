package com.ai.workflow.engine.executor;

import com.ai.workflow.audio.AudioSynthesisResult;
import com.ai.workflow.audio.AudioSynthesisService;
import com.ai.workflow.engine.ExecutionContext;
import com.ai.workflow.engine.NodeExecutor;
import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.enums.NodeType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AudioSynthesisNodeExecutor implements NodeExecutor {

    private final AudioSynthesisService audioSynthesisService;
    private final ObjectMapper objectMapper;

    @Override
    public NodeType supportedType() {
        return NodeType.AUDIO_SYNTHESIS;
    }

    @Override
    public Object execute(WorkflowNode node, ExecutionContext context) {
        try {
            // Parse node config
            JsonNode config = node.getConfig() != null
                    ? objectMapper.readTree(node.getConfig())
                    : objectMapper.createObjectNode();

            String voiceId = config.has("voiceId") ? config.get("voiceId").asText() : "alloy";
            double speed = config.has("speed") ? config.get("speed").asDouble() : 1.0;
            String format = config.has("format") ? config.get("format").asText() : "mp3";

            // Get text from upstream output
            String text = extractTextFromUpstream(node, context);

            // Call audio synthesis
            AudioSynthesisResult result = audioSynthesisService.synthesize(text, voiceId, speed);

            Map<String, Object> output = new HashMap<>();
            output.put("audioUrl", result.getAudioUrl());
            output.put("text", text);
            output.put("format", format);
            output.put("durationSeconds", result.getDurationSeconds());
            context.setNodeOutput(node.getNodeKey(), output);
            return output;

        } catch (Exception e) {
            throw new com.ai.workflow.exception.NodeExecutionException(
                    "Audio synthesis node execution failed: " + e.getMessage(), e);
        }
    }

    private String extractTextFromUpstream(WorkflowNode node, ExecutionContext context) {
        // Get text from the most recent upstream node output
        Object lastOutput = context.getLastNodeOutput();
        if (lastOutput instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) lastOutput;
            Object textObj = map.get("text");
            if (textObj != null) return textObj.toString();
        }
        // Fallback to user input
        return context.getUserInput();
    }
}
