package com.waf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("project", "Web Request Security Gateway");
        response.put("status", "running");
        response.put("endpoints", new String[]{"/api/health", "/api/search?q=test"});
        return response;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "ok");
        response.put("service", "waf-demo");
        return response;
    }

    @GetMapping("/api/search")
    public Map<String, String> search(@RequestParam(defaultValue = "example") String q) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("query", q);
        response.put("message", "Request allowed by security filter.");
        return response;
    }
}
