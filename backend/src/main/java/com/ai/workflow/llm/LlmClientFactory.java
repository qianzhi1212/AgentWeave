package com.ai.workflow.llm;

import com.ai.workflow.entity.LlmProvider;
import com.ai.workflow.repository.LlmProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LlmClientFactory {

    private final LlmProviderRepository providerRepository;
    private final ConcurrentHashMap<Long, LlmClient> clientCache = new ConcurrentHashMap<>();

    @Value("${llm.client.connect-timeout:10000}")
    private int connectTimeout;

    @Value("${llm.client.read-timeout:60000}")
    private int readTimeout;

    public LlmClient getClient(Long providerId) {
        return clientCache.computeIfAbsent(providerId, id -> {
            LlmProvider provider = providerRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("LLM Provider not found: " + id));
            return new OpenAiCompatibleClient(
                    provider.getBaseUrl(), provider.getApiKey(), provider.getModel(),
                    connectTimeout, readTimeout);
        });
    }

    public LlmClient getDefaultClient() {
        LlmProvider defaultProvider = providerRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new IllegalArgumentException("No default LLM provider configured"));
        return getClient(defaultProvider.getId());
    }

    public void evictClient(Long providerId) {
        clientCache.remove(providerId);
    }
}
