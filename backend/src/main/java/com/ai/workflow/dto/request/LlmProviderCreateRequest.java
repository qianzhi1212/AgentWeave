package com.ai.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LlmProviderCreateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String baseUrl;
    @NotBlank
    private String apiKey;
    @NotBlank
    private String model;
    private Boolean isDefault;
}
