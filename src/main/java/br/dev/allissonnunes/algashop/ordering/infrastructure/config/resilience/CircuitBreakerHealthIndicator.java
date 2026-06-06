package br.dev.allissonnunes.algashop.ordering.infrastructure.config.resilience;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.health.contributor.Status;
import org.springframework.cloud.circuitbreaker.retry.CircuitBreakerRetryPolicy;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.dev.allissonnunes.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfiguration.PRODUCT_CATALOG_CB_ID;
import static br.dev.allissonnunes.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfiguration.RAPIDEX_API_CB_ID;

@Component("circuit-breakers")
public class CircuitBreakerHealthIndicator implements HealthIndicator {

    private final List<FrameworkRetryCircuitBreaker> circuitBreakers = new ArrayList<>();

    public CircuitBreakerHealthIndicator(final CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.circuitBreakers.add((FrameworkRetryCircuitBreaker) circuitBreakerFactory.create(PRODUCT_CATALOG_CB_ID));
        this.circuitBreakers.add((FrameworkRetryCircuitBreaker) circuitBreakerFactory.create(RAPIDEX_API_CB_ID));
    }

    @Override
    public @Nullable Health health() {
        final Map<String, Object> indicatorDetails = new HashMap<>();
        String indicatorStatus = Status.UP.getCode();
        Throwable lastException = null;

        for (final FrameworkRetryCircuitBreaker circuitBreaker : this.circuitBreakers) {
            final CircuitBreakerRetryPolicy policy = circuitBreaker.getConfig().getCircuitBreakerRetryPolicy();
            final CircuitBreakerRetryPolicy.State state = policy.getState();

            final Map<String, Object> detailsBuilder = new HashMap<>();
            detailsBuilder.put("state", state.name());

            if (state == CircuitBreakerRetryPolicy.State.OPEN) {
                indicatorStatus = "DEGRADED";
                final Throwable cause = NestedExceptionUtils.getRootCause(policy.getLastException());
                if (cause != null) {
                    lastException = cause;
                    detailsBuilder.put("lastException", lastException.getMessage());
                } else {
                    detailsBuilder.put("lastException", null);
                }
            }

            indicatorDetails.put(circuitBreaker.getId(), detailsBuilder);
        }

        final Health.Builder healthBuilder = Health
                .status(indicatorStatus)
                .withDetails(indicatorDetails);

        if ("DEGRADED".equals(indicatorStatus) && lastException != null) {
            healthBuilder.withException(lastException);
        }

        return healthBuilder.build();
    }

}
