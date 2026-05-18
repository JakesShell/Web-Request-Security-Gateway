package com.waf.controller;

import com.waf.model.GatewayEvent;
import com.waf.model.SecurityDecision;
import com.waf.service.EventLogService;
import com.waf.service.HeaderPolicyService;
import com.waf.service.InspectionService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GatewayApiController {
    private final InspectionService inspectionService;
    private final EventLogService eventLogService;
    private final HeaderPolicyService headerPolicyService;

    public GatewayApiController(InspectionService inspectionService, EventLogService eventLogService, HeaderPolicyService headerPolicyService) {
        this.inspectionService = inspectionService;
        this.eventLogService = eventLogService;
        this.headerPolicyService = headerPolicyService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "GateWatch API Security And Traffic Defense Console");
        response.put("status", "healthy");
        response.put("timestamp", Instant.now().toString());
        response.put("safeDemoBoundary", "Defensive gateway simulation for owned applications, labs, and portfolio review.");
        return response;
    }

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        return eventLogService.metrics();
    }

    @GetMapping("/events")
    public Object events() {
        return eventLogService.recentEvents();
    }

    @PostMapping("/events/clear")
    public Map<String, Object> clearEvents() {
        eventLogService.clear();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "cleared");
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    @GetMapping("/rules")
    public Object rules() {
        return inspectionService.rules();
    }

    @GetMapping("/headers")
    public Object headers() {
        return headerPolicyService.hardenedHeaders();
    }

    @GetMapping("/report")
    public Map<String, Object> report() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("generatedAt", Instant.now().toString());
        response.put("project", "GateWatch API Security And Traffic Defense Console");
        response.put("safeDemoBoundary", "Defensive request inspection demo for authorized environments only.");
        response.put("metrics", eventLogService.metrics());
        response.put("rules", inspectionService.rules());
        response.put("hardenedHeaders", headerPolicyService.hardenedHeaders());
        response.put("events", eventLogService.recentEvents());
        return response;
    }

    @PostMapping("/simulate/{scenario}")
    public Map<String, Object> simulate(@PathVariable String scenario) {
        GatewayEvent event;

        switch (scenario) {
            case "allowed":
                event = runSimulatedRequest("GET", "/gateway/search", "topic=cloud-readiness", "10.0.0.21", "Demo Browser", "simulator");
                break;
            case "suspicious-query":
                event = runSimulatedRequest("GET", "/gateway/search", "demo_sql_probe=true&customer=training", "10.0.0.34", "Demo Browser", "simulator");
                break;
            case "script-like":
                event = runSimulatedRequest("GET", "/gateway/search", "demo_script_probe=true&field=profile", "10.0.0.39", "Demo Browser", "simulator");
                break;
            case "path-probe":
                event = runSimulatedRequest("GET", "/gateway/demo_path_probe/config", "", "10.0.0.42", "Demo Browser", "simulator");
                break;
            case "automation":
                event = runSimulatedRequest("GET", "/gateway/search", "topic=inventory", "10.0.0.45", "sqlmap training-user-agent", "simulator");
                break;
            case "rate-limit":
                event = null;
                for (int i = 0; i < 10; i++) {
                    event = runSimulatedRequest("GET", "/gateway/search", "rate-limit-demo=" + i, "10.0.0.88", "Demo Browser", "simulator");
                }
                break;
            default:
                event = runSimulatedRequest("GET", "/gateway/search", "topic=unknown-demo", "10.0.0.99", "Demo Browser", "simulator");
                break;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scenario", scenario);
        response.put("event", event);
        response.put("metrics", eventLogService.metrics());
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    private GatewayEvent runSimulatedRequest(String method, String path, String query, String sourceIp, String userAgent, String sourceType) {
        SecurityDecision decision = inspectionService.inspect(method, path, query, sourceIp, userAgent);
        return eventLogService.recordRequest(method, path, query, decision, sourceIp, userAgent, sourceType);
    }
}
