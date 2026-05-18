package com.waf.service;

import com.waf.model.HeaderInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class HeaderPolicyService {
    public List<HeaderInfo> hardenedHeaders() {
        return Arrays.asList(
                new HeaderInfo("X-Content-Type-Options", "nosniff", "Reduces MIME-sniffing risk for browser responses.", "active"),
                new HeaderInfo("X-Frame-Options", "DENY", "Helps prevent clickjacking through hidden frames.", "active"),
                new HeaderInfo("Referrer-Policy", "no-referrer", "Limits referrer leakage between sites.", "active"),
                new HeaderInfo("Permissions-Policy", "camera=(), microphone=(), geolocation=()", "Restricts sensitive browser features by default.", "active"),
                new HeaderInfo("Content-Security-Policy", "default-src 'self'", "Limits where scripts, styles, images, and other resources can load from.", "active"),
                new HeaderInfo("Cache-Control", "no-store", "Reduces sensitive response caching for gateway-managed traffic.", "active")
        );
    }
}
