# GateWatch Cloud Load Balancer Readiness

Recommended cloud edge layout:

1. HTTPS load balancer receives external traffic.
2. Managed cloud WAF evaluates broad edge protections.
3. GateWatch receives routed application traffic.
4. GateWatch applies app-layer request decision logging.
5. Events are exported to SIEM and tickets are created for blocked traffic.

Health check path: /api/health

Recommended application port: 8080
