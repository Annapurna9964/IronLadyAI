package com.ironlady.reco.ai;

public interface ExplanationProvider {
    String buildReason(String course, String careerStage, String goal, String challenge, String fallbackReason);
}
