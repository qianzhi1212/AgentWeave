package com.ai.workflow.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LlmProviderResponse {
    private Long id;
    private String name;
    private String baseUrl;
    private String apiKeyMasked;
    private String model;
    private Boolean isDefault;
}
