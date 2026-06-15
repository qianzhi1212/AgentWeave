package com.ai.workflow.audio;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "audio.synthesis.provider", havingValue = "mock", matchIfMissing = true)
public class MockAudioSynthesisService implements AudioSynthesisService {

    @Override
    public AudioSynthesisResult synthesize(String text, String voiceId, double speed) {
        // Simulate processing - generate a mock audio URL
        String mockAudioUrl = "https://mock-audio.local/" + UUID.randomUUID() + ".mp3";
        // Estimate duration based on text length and speed
        int estimatedDuration = (int) Math.ceil(text.length() / (10.0 * speed));
        return AudioSynthesisResult.success(mockAudioUrl, "mp3", estimatedDuration);
    }

    @Override
    public boolean isAvailable() {
        return true; // Mock is always available
    }
}
