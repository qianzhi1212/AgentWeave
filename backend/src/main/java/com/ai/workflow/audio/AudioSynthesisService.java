package com.ai.workflow.audio;

public interface AudioSynthesisService {

    AudioSynthesisResult synthesize(String text, String voiceId, double speed);

    boolean isAvailable();
}
