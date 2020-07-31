package com.tieto.core.sus;

import com.tieto.core.sus.config.RateLimitConfig;
import io.github.bucket4j.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@Component
@Slf4j
public class RateLimitingFilter implements javax.servlet.Filter {
    private Bucket bucket;

    private RateLimitConfig config;
    private Bandwidth limit;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        limit = Bandwidth.classic(config.getRateLimitPerMinute(), Refill.intervally(config.getRateLimitPerMinute(), Duration.ofMinutes(1)));
        this.bucket = Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("Current rate: {}, config: {}", limit.getCapacity(), config.getRateLimitPerMinute());
        if (config.getRateLimitPerMinute() != limit.getCapacity()) {
            log.debug("Reconfiguring rate limit...");
            limit = Bandwidth.classic(config.getRateLimitPerMinute(), Refill.intervally(config.getRateLimitPerMinute(), Duration.ofMinutes(1)));
            this.bucket = Bucket4j.builder()
                    .addLimit(limit)
                    .build();
        }
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            log.warn("Too many requests, {}", request);
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("text/plain");
            httpResponse.setStatus(429);
            httpResponse.getWriter().append("Too many requests");
        }
    }

    public RateLimitConfig getConfig() {
        return config;
    }

    @Autowired
    public void setConfig(RateLimitConfig config) {
        this.config = config;
    }
}
