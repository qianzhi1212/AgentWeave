package com.ai.workflow.llm;

public interface LlmClient {

    String chatCompletion(String systemPrompt, String userPrompt, double temperature, int maxTokens);

    boolean testConnection();
}
