package com.ironlady.reco.dto;

import jakarta.validation.constraints.NotBlank;

public class UserInput {
    @NotBlank
    private String careerStage; // starting, working, mid, senior

    @NotBlank
    private String goal; // leadership, growth, strategic, income

    @NotBlank
    private String challenge; // confidence, politics, stagnation, direction

    public String getCareerStage() {
        return careerStage;
    }

    public void setCareerStage(String careerStage) {
        this.careerStage = normalize(careerStage);
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = normalize(goal);
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = normalize(challenge);
    }

    private String normalize(String s) {
        if (s == null) return null;
        return s.trim().toLowerCase()
                .replace("mid-career", "mid")
                .replace("working professional", "working")
                .replace("starting career", "starting")
                .replace("senior/entrepreneur", "senior")
                .replace("leadership skills", "leadership")
                .replace("career growth", "growth")
                .replace("break barriers / strategic thinking", "strategic")
                .replace("high income / executive influence", "income")
                .replace("lack of confidence", "confidence")
                .replace("office politics", "politics")
                .replace("career stagnation", "stagnation")
                .replace("need strategic direction / network", "direction");
    }
}
