package com.ironlady.reco.ai;

import org.springframework.stereotype.Component;

@Component
public class RuleExplanationProvider implements ExplanationProvider {
    @Override
    public String buildReason(String course, String careerStage, String goal, String challenge, String fallbackReason) {
        return fallbackReason; // default just returns the rule-based string
    }
}
