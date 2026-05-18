package com.waf.model;

public class SecurityRule {
    private final String id;
    private final String category;
    private final String severity;
    private final String title;
    private final String description;
    private final String recommendation;
    private final boolean enabled;

    public SecurityRule(String id, String category, String severity, String title, String description, String recommendation, boolean enabled) {
        this.id = id;
        this.category = category;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.recommendation = recommendation;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getSeverity() {
        return severity;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
