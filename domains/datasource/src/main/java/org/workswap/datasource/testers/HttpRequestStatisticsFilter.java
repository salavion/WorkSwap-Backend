package org.workswap.security.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpRequestStatisticsFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestStatisticsFilter.class);

    private final QueryCounter queryCounter;

    public HttpRequestStatisticsFilter(QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.nanoTime();

        queryCounter.reset();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.nanoTime() - start;
            double durationMs = duration / 1_000_000.0;

            if (durationMs > 100 || queryCounter.getCount() > 10) {
                logger.warn(
                    "SLOW REQUEST: {} {} -> {} | {} ms | {} SQL",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    String.format("%.2f", durationMs),
                    queryCounter.getCount()
                );
            } else {
                logger.info(
                    "{} {} -> {} | {} ms | {} SQL queries",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    String.format("%.2f", durationMs),
                    queryCounter.getCount()
                );
            }

            queryCounter.reset();
        }
    }
}