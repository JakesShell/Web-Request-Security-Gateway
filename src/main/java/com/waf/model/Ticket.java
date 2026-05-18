package com.waf.model;

public class Ticket {
    private String id;
    private String createdAt;
    private String status;
    private String severity;
    private String title;
    private String linkedEventId;
    private String owner;
    private String recommendation;

    public Ticket() {
    }

    public Ticket(String id, String createdAt, String status, String severity, String title, String linkedEventId, String owner, String recommendation) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
        this.severity = severity;
        this.title = title;
        this.linkedEventId = linkedEventId;
        this.owner = owner;
        this.recommendation = recommendation;
    }

    public String getId() { return id; }
    public String getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
    public String getSeverity() { return severity; }
    public String getTitle() { return title; }
    public String getLinkedEventId() { return linkedEventId; }
    public String getOwner() { return owner; }
    public String getRecommendation() { return recommendation; }

    public void setStatus(String status) {
        this.status = status;
    }
}
