package com.ai.workflow.llm;

import com.ai.workflow.llm.exception.LlmCallException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

public class OpenAiCompatibleClient implements LlmClient {

    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OpenAiCompatibleClient(String baseUrl, String apiKey, String model) {
        this(baseUrl, apiKey, model, 10000, 60000);
    }

    public OpenAiCompatibleClient(String baseUrl, String apiKey, String model,
                                  int connectTimeoutMs, int readTimeoutMs) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = new ObjectMapper();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public String chatCompletion(String systemPrompt, String userPrompt, double temperature, int maxTokens) {
        try {
            // Build request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);

            ArrayNode messages = requestBody.putArray("messages");
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                ObjectNode systemMsg = messages.addObject();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
            }
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", userPrompt);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Make HTTP request
            String responseJson = restClient.post()
                    .uri("/chat/completions")
                    .body(jsonBody)
                    .retrieve()
                    .body(String.class);

            // Parse response
            JsonNode responseNode = objectMapper.readTree(responseJson);
            JsonNode choices = responseNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    return message.get("content").asText();
                }
            }

            throw new LlmCallException("Invalid response format from LLM API");

        } catch (LlmCallException e) {
            throw e;
        } catch (Exception e) {
            throw new LlmCallException("LLM API call failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean testConnection() {
        try {
            String result = chatCompletion("", "Hello", 0.0, 5);
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }
}
