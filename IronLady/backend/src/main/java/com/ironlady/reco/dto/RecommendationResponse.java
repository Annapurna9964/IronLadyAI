package com.ironlady.reco.dto;

public class RecommendationResponse {
    private String courseName;
    private String reason;
    private java.util.List<String> tips;

    public RecommendationResponse() {}

    public RecommendationResponse(String courseName, String reason) {
        this.courseName = courseName;
        this.reason = reason;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public java.util.List<String> getTips() {
        return tips;
    }

    public void setTips(java.util.List<String> tips) {
        this.tips = tips;
    }
}
