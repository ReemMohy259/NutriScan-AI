package gov.iti.jets.NutriScan.ratelimit;

import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler) throws IOException {

        if (!(handler instanceof HandlerMethod handlerMethod)) {

            return true;
        }

        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true;
        }

        String key = resolveKey(request, rateLimit.keyType(), handlerMethod);

        ConsumptionProbe probe = rateLimiterService
            .tryConsume(key, rateLimit.limit(), rateLimit.durationSeconds());

        response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));

        if (probe.isConsumed()) {
            return true;
        }

        long retryAfter = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());

        if (retryAfter == 0 && probe.getNanosToWaitForRefill() > 0) {
            retryAfter = 1;
        }

        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(retryAfter));
        response.setContentType("application/json");
        response.getWriter().write("""
            {"error": "rate_limit_exceeded", "retryAfterSeconds": %d}
            """.formatted(retryAfter));
        return false;
    }

    private String resolveKey(
        HttpServletRequest request,
        RateLimitKeyType keyType,
        HandlerMethod handlerMethod) {
        String endpointId = handlerMethod.getBeanType().getSimpleName() + "#"
            + handlerMethod.getMethod().getName();

        if (keyType == RateLimitKeyType.IP) {
            return "rate_limit:ip:%s:%s".formatted(endpointId, resolveClientIp(request));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return "rate_limit:ip:%s:%s".formatted(endpointId, resolveClientIp(request));
        }

        String userId = jwt.getSubject();
        return "rate_limit:user:%s:%s".formatted(endpointId, userId);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
