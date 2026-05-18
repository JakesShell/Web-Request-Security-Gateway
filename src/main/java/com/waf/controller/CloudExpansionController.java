package com.waf.controller;

import com.waf.model.GatewayEvent;
import com.waf.model.Ticket;
import com.waf.service.EventLogService;
import com.waf.service.TicketQueueService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CloudExpansionController {
    private final EventLogService eventLogService;
    private final TicketQueueService ticketQueueService;

    public CloudExpansionController(EventLogService eventLogService, TicketQueueService ticketQueueService) {
        this.eventLogService = eventLogService;
        this.ticketQueueService = ticketQueueService;
    }

    @GetMapping("/tickets")
    public Object tickets() {
        return ticketQueueService.tickets();
    }

    @PostMapping("/tickets/{ticketId}/status")
    public Object updateTicket(@PathVariable String ticketId, @RequestBody Map<String, String> payload) {
        Ticket ticket = ticketQueueService.updateStatus(ticketId, payload.get("status"));

        if (ticket == null) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("error", "Ticket not found.");
            response.put("ticketId", ticketId);
            return response;
        }

        return ticket;
    }

    @GetMapping("/siem/export")
    public Object siemExport() {
        List<Map<String, Object>> records = new ArrayList<>();

        for (GatewayEvent event : eventLogService.recentEvents()) {
            Map<String, Object> record = new LinkedHashMap<>();
            record.put("@timestamp", event.getTimestamp());
            record.put("event.kind", "alert");
            record.put("event.category", "web");
            record.put("event.action", event.getDecision());
            record.put("event.severity", event.getSeverity());
            record.put("rule.id", event.getRuleId());
            record.put("source.ip", event.getSourceIp());
            record.put("http.request.method", event.getMethod());
            record.put("url.path", event.getPath());
            record.put("url.query", event.getQuery());
            record.put("user_agent.original", event.getUserAgent());
            records.add(record);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("format", "siem-style-json");
        response.put("recordCount", records.size());
        response.put("records", records);
        return response;
    }

    @GetMapping("/ai/summary")
    public Object aiSummary() {
        List<GatewayEvent> events = eventLogService.recentEvents();

        long blocked = events.stream().filter(event -> "blocked".equals(event.getDecision())).count();
        long high = events.stream().filter(event -> "high".equals(event.getSeverity())).count();

        String summary;
        if (high > 0) {
            summary = "High-severity gateway events are active. Prioritize ticket review, source pattern analysis, and cloud WAF policy alignment.";
        } else if (blocked > 0) {
            summary = "Blocked traffic is present. Review the event queue, tune rules, and export SIEM records for operational review.";
        } else {
            summary = "No high-risk gateway events are active in the current window. Continue monitoring after deployment changes.";
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mode", "local-rule-based-ai-assist");
        response.put("generatedAt", Instant.now().toString());
        response.put("summary", summary);
        response.put("recommendedNextActions", new String[] {
                "Review open tickets",
                "Export SIEM event package",
                "Compare GateWatch rules with cloud WAF coverage",
                "Validate load balancer health check path"
        });
        return response;
    }

    @GetMapping("/rbac/session")
    public Object rbac(@RequestHeader(value = "X-GateWatch-Role", required = false) String role) {
        String resolved = role == null || role.isBlank() ? "admin" : role.toLowerCase();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("role", resolved);
        response.put("permissions", "admin".equals(resolved)
                ? new String[] {"view", "simulate", "clear-events", "import-policy", "export-siem", "update-ticket"}
                : new String[] {"view", "export-siem"});
        response.put("note", "Demo RBAC is header-based using X-GateWatch-Role.");
        return response;
    }

    @PostMapping("/policies/import")
    public Object importPolicy(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("importedAt", Instant.now().toString());
        response.put("source", payload.getOrDefault("source", "demo-policy-upload"));
        response.put("policyName", payload.getOrDefault("policyName", "GateWatch Demo Gateway Policy"));
        response.put("status", "validated-demo-import");
        response.put("mappedControls", new String[] {"rate-limit", "header-hardening", "request-decision-logging", "ticket-creation", "siem-export"});
        return response;
    }

    @GetMapping("/cloud/readiness")
    public Object cloudReadiness() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("healthCheckPath", "/api/health");
        response.put("containerPort", 8080);
        response.put("recommendedReplicas", 2);
        response.put("loadBalancerMode", "HTTPS edge load balancer in front of GateWatch");
        response.put("networkControls", new String[] {"TLS termination", "private service networking", "restricted admin endpoints", "centralized logs"});
        response.put("readinessStatus", "cloud-ready demo structure");
        return response;
    }

    @GetMapping("/cloud/waf-comparison")
    public Object wafComparison() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("gatewatchRules", new String[] {"GW-RATE-01", "GW-PATH-01", "GW-SCRIPT-01", "GW-QUERY-01", "GW-AUTO-01"});
        response.put("awsWafMapping", new String[] {"Rate-based rule", "Admin path block list", "Query anomaly signal", "Bot/user-agent signal"});
        response.put("azureWafMapping", new String[] {"Rate limit policy", "Custom rule match conditions", "Managed rule review", "Bot protection policy"});
        response.put("cloudflareMapping", new String[] {"Rate limiting rules", "WAF custom rules", "Bot controls", "Firewall events export"});
        response.put("recommendation", "Use GateWatch as an application-layer observability and decision console beside managed cloud WAF controls.");
        return response;
    }

    @GetMapping("/cloud/kubernetes-manifests")
    public Object kubernetesManifests() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("deployment", "deploy/kubernetes/gatewatch-deployment.yaml");
        response.put("service", "deploy/kubernetes/gatewatch-service.yaml");
        response.put("ingress", "deploy/kubernetes/gatewatch-ingress.yaml");
        response.put("note", "Manifests are demo-ready and should be adjusted for real cluster domains, TLS secrets, and image registry.");
        return response;
    }
}
