package com.hugo.mabibli.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@ConditionalOnProperty(
        prefix = "app.rate-limit",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Window> windows =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Limit limit = findLimit(request);

        if (limit == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String key =
                request.getRemoteAddr()
                        + ":"
                        + request.getMethod()
                        + ":"
                        + request.getRequestURI();

        if (!isAllowed(key, limit)) {
            response.setStatus(
                    HttpStatus.TOO_MANY_REQUESTS.value()
            );

            response.setContentType(
                    MediaType.APPLICATION_PROBLEM_JSON_VALUE
            );

            response.setHeader(
                    "Retry-After",
                    String.valueOf(limit.window().toSeconds())
            );

            response.getWriter().write("""
                    {
                      "title": "Too Many Requests",
                      "status": 429,
                      "code": "RATE_LIMIT_EXCEEDED",
                      "detail": "Trop de requêtes. Réessaie plus tard."
                    }
                    """);

            return;
        }

        filterChain.doFilter(request, response);
    }

    private Limit findLimit(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if ("POST".equals(method)
                && "/api/auth/login".equals(path)) {
            return new Limit(10, Duration.ofMinutes(1));
        }

        if ("POST".equals(method)
                && "/api/auth/register".equals(path)) {
            return new Limit(5, Duration.ofHours(1));
        }

        if ("GET".equals(method)
                && "/api/open-library/search".equals(path)) {
            return new Limit(30, Duration.ofMinutes(1));
        }

        return null;
    }

    private boolean isAllowed(
            String key,
            Limit limit
    ) {
        long now = System.currentTimeMillis();
        AtomicBoolean allowed = new AtomicBoolean();

        windows.compute(key, (ignored, current) -> {
            long windowLength =
                    limit.window().toMillis();

            Window activeWindow = current;

            if (activeWindow == null
                    || now - activeWindow.startedAt()
                    >= windowLength) {
                activeWindow = new Window(now, 0);
            }

            int nextCount =
                    activeWindow.requestCount() + 1;

            allowed.set(nextCount <= limit.maximumRequests());

            return new Window(
                    activeWindow.startedAt(),
                    nextCount
            );
        });

        if (windows.size() > 10_000) {
            windows.entrySet().removeIf(entry ->
                    now - entry.getValue().startedAt()
                            > Duration.ofHours(2).toMillis()
            );
        }

        return allowed.get();
    }

    private record Limit(
            int maximumRequests,
            Duration window
    ) {
    }

    private record Window(
            long startedAt,
            int requestCount
    ) {
    }
}