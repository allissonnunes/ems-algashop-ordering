package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.BadGatewayException;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.GatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;

import static br.dev.allissonnunes.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfiguration.RAPIDEX_API_CB_ID;

@Slf4j
@Component
public class ResilientRapidexClient {

    private final RapidexClient client;

    private final CircuitBreaker circuitBreaker;

    public ResilientRapidexClient(
            final RapidexClient client,
            final CircuitBreakerFactory<?, ?> circuitBreakerFactory
    ) {
        this.client = client;
        this.circuitBreaker = circuitBreakerFactory.create(RAPIDEX_API_CB_ID);
    }

    @ConcurrencyLimit(15)
    public DeliveryCostResponse calculate(final DeliveryCostRequest request) {
        try {
            final DeliveryCostResponse response = this.circuitBreaker.run(
                    () -> doCalculate(request),
                    ex -> doInternalFallback(request, ex)
            );
            if (response == null) {
                throw new BadGatewayException.ClientErrorException("Invalid zip code provided", null);
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DeliveryCostResponse doCalculate(final DeliveryCostRequest request) {
        try {
            return client.calculate(request);
        } catch (final HttpClientErrorException e) {
            if (!(e instanceof HttpClientErrorException.NotFound)) {
                log.atWarn().setMessage("Client error when loading delivery cost {}").addArgument(request).setCause(e).log();
            }
            return null;
        } catch (final RestClientException e) {
            throw translateException(e);
        }
    }

    private DeliveryCostResponse doInternalFallback(final DeliveryCostRequest request, final @Nullable Throwable ex) {
        log.atWarn().setMessage("Rapidex API call failed for request {}").addArgument(request).setCause(ex).log();
        return new DeliveryCostResponse("20.0", 10L);
    }

    private RuntimeException translateException(final RestClientException e) {
        if (e.getCause() instanceof SocketTimeoutException || e instanceof ResourceAccessException) {
            return new GatewayTimeoutException("Rapidex API Timeout", e);
        }

        if (e instanceof HttpClientErrorException) {
            return new BadGatewayException.ClientErrorException("Rapidex API Client Error", e);
        }

        if (e instanceof HttpServerErrorException) {
            return new BadGatewayException.ServerErrorException("Rapidex API Internal Error", e);
        }

        return new BadGatewayException("Rapidex API Bad Gateway", e);
    }

}
