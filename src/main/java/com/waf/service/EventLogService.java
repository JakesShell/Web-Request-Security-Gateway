package com.waf.service;

import com.waf.model.GatewayEvent;
import com.waf.model.SecurityDecision;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class EventLogService {
    private final CopyOnWriteArrayList<GatewayEvent> events = new CopyOnWriteArrayList<>();

    public GatewayEvent recordRequest(String method, String path, String query, SecurityDecision decision, String sourceIp, String userAgent, String sourceType) {
        GatewayEvent event = new GatewayEvent(
                UUID.randomUUID().toString().substring(0, 8),
                Instant.now().toString(),
                method,
                path,
                query == null ? "" : query,
                decision.isAllowed() ? "allowed" : "blocked",
                decision.getSeverity(),
                decision.getRuleId(),
                decision.getCategory(),
                decision.getExplanation(),
                decision.getRecommendation(),
                sourceIp == null ? "unknown" : sourceIp,
                userAgent == null ? "unknown" : userAgent,
                sourceType
        );

        events.add(0, event);

        while (events.size() > 200) {
            events.remove(events.size() - 1);
        }

        return event;
    }

    public List<GatewayEvent> recentEvents() {
        return new ArrayList<>(events);
    }

    public void clear() {
        events.clear();
    }

    public Map<String, Object> metrics() {
        long total = events.size();
        long blocked = events.stream().filter(event -> "blocked".equals(event.getDecision())).count();
        long allowed = events.stream().filter(event -> "allowed".equals(event.getDecision())).count();
        long high = events.stream().filter(event -> "high".equals(event.getSeverity())).count();
        long medium = events.stream().filter(event -> "medium".equals(event.getSeverity())).count();
        long low = events.stream().filter(event -> "low".equals(event.getSeverity())).count();

        int riskScore = calculateGatewayReadinessScore(blocked, high, medium);

        Map<String, Long> topRules = events.stream()
                .filter(event -> event.getRuleId() != null)
                .collect(Collectors.groupingBy(GatewayEvent::getRuleId, LinkedHashMap::new, Collectors.counting()));

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalRequests", total);
        metrics.put("allowedRequests", allowed);
        metrics.put("blockedRequests", blocked);
        metrics.put("highSeverityEvents", high);
        metrics.put("mediumSeverityEvents", medium);
        metrics.put("lowSeverityEvents", low);
        metrics.put("gatewayReadinessScore", riskScore);
        metrics.put("rating", ratingFromScore(riskScore));
        metrics.put("topTriggeredRules", topRules);
        metrics.put("executiveSummary", buildSummary(blocked, high, medium, riskScore));

        return metrics;
    }

    private int calculateGatewayReadinessScore(long blocked, long high, long medium) {
        long penalty = (high * 18) + (medium * 9) + (blocked * 4);
        int score = (int) Math.max(0, Math.min(100, 100 - penalty));
        return score;
    }

    private String ratingFromScore(int score) {
        if (score >= 85) {
            return "strong";
        }
        if (score >= 70) {
            return "moderate";
        }
        if (score >= 50) {
            return "watch";
        }
        return "critical";
    }

    private String buildSummary(long blocked, long high, long medium, int score) {
        if (high > 0) {
            return "High-priority request patterns were blocked. Review source patterns, affected endpoints, and rule coverage before production readiness.";
        }
        if (blocked > 0 || medium > 0) {
            return "GateWatch is actively blocking suspicious traffic. Continue monitoring the event queue and tune rules for the application context.";
        }
        if (score >= 85) {
            return "Gateway traffic looks controlled in the current demo window. Continue monitoring and validate rule coverage after application changes.";
        }
        return "Gateway posture needs review before it should be considered operationally mature.";
    }
}
