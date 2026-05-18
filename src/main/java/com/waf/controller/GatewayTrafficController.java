package com.waf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class GatewayTrafficController {
    @GetMapping("/gateway/search")
    public Map<String, Object> search(String q) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "allowed");
        response.put("message", "Gateway request passed inspection.");
        response.put("query", q == null ? "" : q);
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    @GetMapping("/gateway/orders/status")
    public Map<String, Object> orderStatus() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "allowed");
        response.put("message", "Order status endpoint reached through GateWatch.");
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}
