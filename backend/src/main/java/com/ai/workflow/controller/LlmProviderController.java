package com.ai.workflow.controller;

import com.ai.workflow.dto.request.LlmProviderCreateRequest;
import com.ai.workflow.dto.response.LlmProviderResponse;
import com.ai.workflow.entity.LlmProvider;
import com.ai.workflow.service.LlmProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/llm-providers")
@RequiredArgsConstructor
public class LlmProviderController {

    private final LlmProviderService providerService;

    @PostMapping
    public ResponseEntity<LlmProviderResponse> create(@Valid @RequestBody LlmProviderCreateRequest request) {
        LlmProvider provider = providerService.createProvider(
                request.getName(), request.getBaseUrl(), request.getApiKey(),
                request.getModel(), request.getIsDefault() != null && request.getIsDefault());
        return ResponseEntity.ok(toResponse(provider));
    }

    @GetMapping
    public ResponseEntity<List<LlmProviderResponse>> list() {
        List<LlmProvider> providers = providerService.listProviders();
        return ResponseEntity.ok(providers.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LlmProviderResponse> get(@PathVariable Long id) {
        LlmProvider provider = providerService.getProvider(id);
        return ResponseEntity.ok(toResponse(provider));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LlmProviderResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody LlmProviderCreateRequest request) {
        LlmProvider provider = providerService.updateProvider(
                id, request.getName(), request.getBaseUrl(), request.getApiKey(),
                request.getModel(), request.getIsDefault() != null && request.getIsDefault());
        return ResponseEntity.ok(toResponse(provider));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/test")
    public ResponseEntity<Boolean> testConnection(@PathVariable Long id) {
        boolean result = providerService.testConnection(id);
        return ResponseEntity.ok(result);
    }

    private LlmProviderResponse toResponse(LlmProvider provider) {
        String maskedKey = maskApiKey(provider.getApiKey());
        return LlmProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .baseUrl(provider.getBaseUrl())
                .apiKeyMasked(maskedKey)
                .model(provider.getModel())
                .isDefault(provider.getIsDefault())
                .build();
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) return "****";
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
