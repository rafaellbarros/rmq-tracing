package com.github.rafaellbarros.tracing.core.filter;

import com.github.rafaellbarros.tracing.core.util.TracingUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TracingFilter extends OncePerRequestFilter {

    private final TracingUtil tracingUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = tracingUtil.getCurrentTraceId();
        if (traceId != null) {
            MDC.put("traceId", traceId);
            response.addHeader("X-Trace-Id", traceId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
        }
    }
}