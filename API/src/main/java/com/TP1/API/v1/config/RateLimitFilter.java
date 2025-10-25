package com.TP1.API.v1.config;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${ratelimit.capacity}")
    private int capacity;

    @Value("${ratelimit.refill.tokens}")
    private int refillTokens;

    @Value("${ratelimit.refill.period}")
    private String refillPeriod;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Duration period = Duration.parse("PT" + refillPeriod.toUpperCase().replace("S", "S"));
        Refill refill = Refill.intervally(refillTokens, period);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket resolveBucket(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            userId = request.getRemoteAddr();
        }
        return buckets.computeIfAbsent(userId, k -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Bucket bucket = resolveBucket(request);

        if (!bucket.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"So many request per user\",\"path\":\""
                    + request.getRequestURI() + "\"}");
            response.getWriter().flush();
            return;

        }

        filterChain.doFilter(request, response);
    }
}
