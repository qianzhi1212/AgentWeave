package com.ai.workflow.service;

import com.ai.workflow.entity.LlmProvider;
import com.ai.workflow.llm.LlmClientFactory;
import com.ai.workflow.repository.LlmProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LlmProviderService {

    private final LlmProviderRepository providerRepository;
    private final LlmClientFactory clientFactory;

    @Transactional
    public LlmProvider createProvider(String name, String baseUrl, String apiKey, String model, boolean isDefault) {
        if (isDefault) {
            providerRepository.findByIsDefaultTrue().ifPresent(p -> {
                p.setIsDefault(false);
                providerRepository.save(p);
            });
        }
        LlmProvider provider = LlmProvider.builder()
                .name(name)
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .model(model)
                .isDefault(isDefault)
                .build();
        return providerRepository.save(provider);
    }

    public List<LlmProvider> listProviders() {
        return providerRepository.findAll();
    }

    public LlmProvider getProvider(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LLM Provider not found: " + id));
    }

    @Transactional
    public LlmProvider updateProvider(Long id, String name, String baseUrl, String apiKey,
                                      String model, boolean isDefault) {
        LlmProvider provider = getProvider(id);
        provider.setName(name);
        provider.setBaseUrl(baseUrl);
        provider.setApiKey(apiKey);
        provider.setModel(model);
        provider.setIsDefault(isDefault);
        clientFactory.evictClient(id);
        return providerRepository.save(provider);
    }

    @Transactional
    public void deleteProvider(Long id) {
        clientFactory.evictClient(id);
        providerRepository.deleteById(id);
    }

    public boolean testConnection(Long id) {
        return clientFactory.getClient(id).testConnection();
    }
}
