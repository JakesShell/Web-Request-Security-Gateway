package com.waf.service;

import com.waf.model.SecurityDecision;
import com.waf.model.SecurityRule;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InspectionService {
    private final List<SecurityRule> rules;
    private final Map<String, Deque<Instant>> rateTracker = new ConcurrentHashMap<>();

    public InspectionService() {
        this.rules = Arrays.asList(
                new SecurityRule(
                        "GW-RATE-01",
                        "Traffic Volume",
                        "medium",
                        "Rapid repeated requests",
                        "A source generated repeated requests in a short time window.",
                        "Apply rate limiting, review client behavior, and validate whether the source is automated or expected.",
                        true
                ),
                new SecurityRule(
                        "GW-PATH-01",
                        "Path Probing",
                        "high",
                        "Sensitive path probe",
                        "The request path matched a sensitive or administrative probing pattern.",
                        "Block the request, review logs, and confirm no sensitive files or admin routes are exposed.",
                        true
                ),
                new SecurityRule(
                        "GW-SCRIPT-01",
                        "Script Injection Probe",
                        "high",
                        "Script-like input pattern",
                        "The request contained a script-like marker or browser-execution indicator.",
                        "Block the request, validate input encoding, and review application output handling.",
                        true
                ),
                new SecurityRule(
                        "GW-QUERY-01",
                        "Query Manipulation Probe",
                        "high",
                        "Suspicious query pattern",
                        "The request query matched a manipulation or database-probing indicator.",
                        "Block the request, validate parameterized data access, and inspect repeated source activity.",
                        true
                ),
                new SecurityRule(
                        "GW-AUTO-01",
                        "Automation Probe",
                        "medium",
                        "Suspicious automation user agent",
                        "The request used a tool-like or automated user-agent pattern.",
                        "Review source reputation, apply challenge controls where appropriate, and monitor for repeat attempts.",
                        true
                )
        );
    }

    public List<SecurityRule> rules() {
        return rules;
    }

    public SecurityDecision inspect(String method, String path, String query, String sourceIp, String userAgent) {
        String safePath = path == null ? "" : path;
        String safeQuery = query == null ? "" : query;
        String safeUserAgent = userAgent == null ? "" : userAgent;
        String combined = (safePath + " " + safeQuery).toLowerCase();

        SecurityRule rateRule = ruleById("GW-RATE-01");
        if (isRateLimited(sourceIp) && rateRule.isEnabled()) {
            return SecurityDecision.blocked(rateRule, 62);
        }

        SecurityRule pathRule = ruleById("GW-PATH-01");
        if (pathRule.isEnabled() && containsAny(combined, "demo_path_probe", "../", "%2e%2e", ".env", "wp-admin", "config.yml", "shadow")) {
            return SecurityDecision.blocked(pathRule, 90);
        }

        SecurityRule scriptRule = ruleById("GW-SCRIPT-01");
        if (scriptRule.isEnabled() && containsAny(combined, "demo_script_probe", "<script", "%3cscript", "javascript:", "onerror=", "onload=")) {
            return SecurityDecision.blocked(scriptRule, 88);
        }

        SecurityRule queryRule = ruleById("GW-QUERY-01");
        if (queryRule.isEnabled() && containsAny(combined, "demo_sql_probe", "union select", "sleep(", "benchmark(", "' or '1'='1")) {
            return SecurityDecision.blocked(queryRule, 86);
        }

        SecurityRule autoRule = ruleById("GW-AUTO-01");
        String lowerAgent = safeUserAgent.toLowerCase();
        if (autoRule.isEnabled() && containsAny(lowerAgent, "sqlmap", "nikto", "masscan", "nmap", "dirbuster")) {
            return SecurityDecision.blocked(autoRule, 64);
        }

        return SecurityDecision.allowed();
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private SecurityRule ruleById(String id) {
        return rules.stream()
                .filter(rule -> id.equals(rule.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing rule " + id));
    }

    private boolean isRateLimited(String sourceIp) {
        String key = sourceIp == null ? "unknown" : sourceIp;
        Instant now = Instant.now();
        Instant cutoff = now.minusSeconds(20);

        Deque<Instant> timestamps = rateTracker.computeIfAbsent(key, ignored -> new ArrayDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
                timestamps.removeFirst();
            }

            timestamps.addLast(now);
            return timestamps.size() > 8;
        }
    }
}
