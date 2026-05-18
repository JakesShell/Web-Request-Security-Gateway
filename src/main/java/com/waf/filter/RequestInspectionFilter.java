package com.waf.filter;

import com.waf.model.SecurityDecision;
import com.waf.service.EventLogService;
import com.waf.service.InspectionService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestInspectionFilter extends OncePerRequestFilter {
    private final InspectionService inspectionService;
    private final EventLogService eventLogService;

    public RequestInspectionFilter(InspectionService inspectionService, EventLogService eventLogService) {
        this.inspectionService = inspectionService;
        this.eventLogService = eventLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (shouldSkip(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String sourceIp = clientIp(request);
        SecurityDecision decision = inspectionService.inspect(
                request.getMethod(),
                path,
                request.getQueryString(),
                sourceIp,
                request.getHeader("User-Agent")
        );

        eventLogService.recordRequest(
                request.getMethod(),
                path,
                request.getQueryString(),
                decision,
                sourceIp,
                request.getHeader("User-Agent"),
                "live_gateway"
        );

        if (!decision.isAllowed()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"decision\":\"blocked\",\"ruleId\":\"" + escape(decision.getRuleId()) + "\",\"severity\":\"" + escape(decision.getSeverity()) + "\",\"message\":\"GateWatch blocked this request for defensive demo purposes.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkip(String path) {
        return path.equals("/")
                || path.startsWith("/api")
                || path.startsWith("/actuator")
                || path.startsWith("/error")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".svg")
                || path.endsWith(".ico");
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
