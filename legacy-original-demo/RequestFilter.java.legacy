package com.waf.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class RequestFilter extends OncePerRequestFilter {

    private static final Pattern SQLI_PATTERN = Pattern.compile(
            "(?i)(union\\s+select|drop\\s+table|or\\s+1=1|\\bselect\\b.*\\bfrom\\b|insert\\s+into|delete\\s+from|update\\s+.+\\s+set)"
    );

    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(?i)(<script|javascript:|onerror=|onload=|alert\\()"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI() == null ? "" : request.getRequestURI();
        String query = request.getQueryString() == null ? "" : request.getQueryString();

        String decodedUri = URLDecoder.decode(uri, StandardCharsets.UTF_8);
        String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);
        String combinedInput = decodedUri + " " + decodedQuery;

        if (SQLI_PATTERN.matcher(combinedInput).find() || XSS_PATTERN.matcher(combinedInput).find()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Request blocked by security filter.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}