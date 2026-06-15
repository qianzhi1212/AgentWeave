package com.ai.workflow.engine.executor;

import com.ai.workflow.engine.ExecutionContext;
import com.ai.workflow.engine.NodeExecutor;
import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.enums.NodeType;
import com.ai.workflow.llm.LlmClient;
import com.ai.workflow.llm.LlmClientFactory;
import com.ai.workflow.llm.OpenAiCompatibleClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LlmNodeExecutor implements NodeExecutor {

    private final LlmClientFactory llmClientFactory;
    private final ObjectMapper objectMapper;

    @Value("${llm.client.connect-timeout:10000}")
    private int connectTimeout;

    @Value("${llm.client.read-timeout:60000}")
    private int readTimeout;

    @Override
    public NodeType supportedType() {
        return NodeType.LLM;
    }

    @Override
    public Object execute(WorkflowNode node, ExecutionContext context) {
        try {
            // Parse node config
            JsonNode config = node.getConfig() != null
                    ? objectMapper.readTree(node.getConfig())
                    : objectMapper.createObjectNode();

            String systemPrompt = config.has("systemPrompt") ? config.get("systemPrompt").asText() : "";
            String userPromptTemplate = config.has("userPromptTemplate") ? config.get("userPromptTemplate").asText() : "{{input}}";
            double temperature = config.has("temperature") ? config.get("temperature").asDouble() : 0.7;
            int maxTokens = config.has("maxTokens") ? config.get("maxTokens").asInt() : 2048;

            // Collect upstream outputs and interpolate template
            String userPrompt = interpolateTemplate(userPromptTemplate, context);

            // Get LLM client: priority 1) node config apiKey  2) assigned provider  3) default provider
            String configApiKey = config.has("apiKey") ? config.get("apiKey").asText() : "";
            String configBaseUrl = config.has("baseUrl") ? config.get("baseUrl").asText() : "";
            String configModel = config.has("model") ? config.get("model").asText() : "";

            LlmClient client;
            if (configApiKey != null && !configApiKey.isBlank()) {
                // Direct API key from node config
                String baseUrl = (configBaseUrl != null && !configBaseUrl.isBlank())
                        ? configBaseUrl : "https://api.deepseek.com/v1";
                String model = (configModel != null && !configModel.isBlank())
                        ? configModel : "deepseek-chat";
                client = new OpenAiCompatibleClient(baseUrl, configApiKey, model, connectTimeout, readTimeout);
            } else {
                Long providerId = node.getLlmProvider() != null ? node.getLlmProvider().getId() : null;
                client = providerId != null ? llmClientFactory.getClient(providerId) : llmClientFactory.getDefaultClient();
            }

            // Call LLM
            var response = client.chatCompletion(systemPrompt, userPrompt, temperature, maxTokens);

            Map<String, Object> output = new HashMap<>();
            output.put("text", response);
            output.put("tokenUsage", 0); // Will be populated from actual response
            context.setNodeOutput(node.getNodeKey(), output);
            return output;

        } catch (Exception e) {
            throw new com.ai.workflow.exception.NodeExecutionException(
                    "LLM node execution failed: " + e.getMessage(), e);
        }
    }

    private String interpolateTemplate(String template, ExecutionContext context) {
        // Replace {{variable}} patterns with upstream node outputs
        String result = template;
        // Simple {{input}} replacement with user input
        result = result.replace("{{input}}", context.getUserInput());
        // Replace {{nodeKey.text}} with specific node outputs
        for (Map.Entry<String, Object> entry : context.getAllNodeOutputs().entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) entry.getValue();
                for (Map.Entry<?, ?> inner : map.entrySet()) {
                    result = result.replace("{{" + entry.getKey() + "." + inner.getKey() + "}}",
                            String.valueOf(inner.getValue()));
                }
            }
        }
        return result;
    }
}
