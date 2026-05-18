package com.waf.model;

public class SecurityDecision {
    private final boolean allowed;
    private final String ruleId;
    private final String category;
    private final String severity;
    private final String explanation;
    private final String recommendation;
    private final int riskScore;

    private SecurityDecision(boolean allowed, String ruleId, String category, String severity, String explanation, String recommendation, int riskScore) {
        this.allowed = allowed;
        this.ruleId = ruleId;
        this.category = category;
        this.severity = severity;
        this.explanation = explanation;
        this.recommendation = recommendation;
        this.riskScore = riskScore;
    }

    public static SecurityDecision allowed() {
        return new SecurityDecision(true, "GW-ALLOW-00", "Allowed Traffic", "low", "Request matched no active defensive rule.", "Allow and continue monitoring.", 5);
    }

    public static SecurityDecision blocked(SecurityRule rule, int riskScore) {
        return new SecurityDecision(false, rule.getId(), rule.getCategory(), rule.getSeverity(), rule.getDescription(), rule.getRecommendation(), riskScore);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getCategory() {
        return category;
    }

    public String getSeverity() {
        return severity;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public int getRiskScore() {
        return riskScore;
    }
}
