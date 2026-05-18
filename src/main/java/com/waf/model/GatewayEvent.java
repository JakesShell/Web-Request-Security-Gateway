package com.waf.model;

public class GatewayEvent {
    private String id;
    private String timestamp;
    private String method;
    private String path;
    private String query;
    private String decision;
    private String severity;
    private String ruleId;
    private String category;
    private String explanation;
    private String recommendation;
    private String sourceIp;
    private String userAgent;
    private String sourceType;

    public GatewayEvent() {
    }

    public GatewayEvent(String id, String timestamp, String method, String path, String query, String decision, String severity, String ruleId, String category, String explanation, String recommendation, String sourceIp, String userAgent, String sourceType) {
        this.id = id;
        this.timestamp = timestamp;
        this.method = method;
        this.path = path;
        this.query = query;
        this.decision = decision;
        this.severity = severity;
        this.ruleId = ruleId;
        this.category = category;
        this.explanation = explanation;
        this.recommendation = recommendation;
        this.sourceIp = sourceIp;
        this.userAgent = userAgent;
        this.sourceType = sourceType;
    }

    public String getId() { return id; }
    public String getTimestamp() { return timestamp; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getQuery() { return query; }
    public String getDecision() { return decision; }
    public String getSeverity() { return severity; }
    public String getRuleId() { return ruleId; }
    public String getCategory() { return category; }
    public String getExplanation() { return explanation; }
    public String getRecommendation() { return recommendation; }
    public String getSourceIp() { return sourceIp; }
    public String getUserAgent() { return userAgent; }
    public String getSourceType() { return sourceType; }

    public void setId(String id) { this.id = id; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setMethod(String method) { this.method = method; }
    public void setPath(String path) { this.path = path; }
    public void setQuery(String query) { this.query = query; }
    public void setDecision(String decision) { this.decision = decision; }
    public void setSeverity(String severity) { this.severity = severity; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public void setCategory(String category) { this.category = category; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
}
