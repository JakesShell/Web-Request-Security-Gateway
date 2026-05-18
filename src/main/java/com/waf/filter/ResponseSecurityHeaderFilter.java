package com.waf.filter;

import com.waf.model.HeaderInfo;
import com.waf.service.HeaderPolicyService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ResponseSecurityHeaderFilter extends OncePerRequestFilter {
    private final HeaderPolicyService headerPolicyService;

    public ResponseSecurityHeaderFilter(HeaderPolicyService headerPolicyService) {
        this.headerPolicyService = headerPolicyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        for (HeaderInfo header : headerPolicyService.hardenedHeaders()) {
            response.setHeader(header.getName(), header.getValue());
        }

        filterChain.doFilter(request, response);
    }
}
