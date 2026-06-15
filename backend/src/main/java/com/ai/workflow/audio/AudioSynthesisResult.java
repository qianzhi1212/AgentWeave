package com.ai.workflow.audio;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudioSynthesisResult {
    private boolean success;
    private String audioUrl;
    private String format;
    private Integer durationSeconds;
    private String errorMessage;

    public static AudioSynthesisResult success(String audioUrl, String format, int durationSeconds) {
        return AudioSynthesisResult.builder()
                .success(true)
                .audioUrl(audioUrl)
                .format(format)
                .durationSeconds(durationSeconds)
                .build();
    }

    public static AudioSynthesisResult failure(String errorMessage) {
        return AudioSynthesisResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
