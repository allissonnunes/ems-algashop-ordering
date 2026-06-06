package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.BadGatewayException;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.GatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.core.retry.RetryException;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.UUID;

import static br.dev.allissonnunes.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfiguration.PRODUCT_CATALOG_CB_ID;

@Slf4j
@Component
class ResilientProductCatalogClient {

    private final ProductCatalogClient productCatalogClient;

    private final CircuitBreaker circuitBreaker;

    public ResilientProductCatalogClient(
            final ProductCatalogClient productCatalogClient,
            final CircuitBreakerFactory<?, ?> circuitBreakerFactory
    ) {
        this.productCatalogClient = productCatalogClient;
        this.circuitBreaker = circuitBreakerFactory.create(PRODUCT_CATALOG_CB_ID);
    }

    @Cacheable(cacheNames = "algashop:product-catalog-api:v1", key = "#productId", unless = "#result == null")
    @ConcurrencyLimit(10)
    public Optional<ProductResponse> findById(final UUID productId) {
        log.atInfo().setMessage("Trying to find product by id {}").addArgument(productId).log();
        try {
            return circuitBreaker.run(() -> loadProduct(productId));
        } catch (final NoFallbackAvailableException e) {
            throw unwrapException(e);
        }
    }

    private @NonNull RuntimeException unwrapException(final NoFallbackAvailableException e) {
        if (e.getCause() instanceof RetryException re) {
            if (re.getCause() instanceof GatewayTimeoutException gte) {
                return gte;
            }
            if (re.getCause() instanceof BadGatewayException bge) {
                return bge;
            }
        }
        return e;
    }

    private @NonNull Optional<ProductResponse> loadProduct(final UUID productId) {
        log.atInfo().setMessage("Call Product Catalog API load product by id {}").addArgument(productId).log();
        try {
            return Optional.of(productCatalogClient.findById(productId));
        } catch (final HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (final RestClientException e) {
            throw translateException(e);
        }
    }

    private RuntimeException translateException(final RestClientException e) {
        if (e.getCause() instanceof SocketTimeoutException || e instanceof ResourceAccessException) {
            return new GatewayTimeoutException("Product Catalog API Timeout", e);
        }

        if (e instanceof HttpClientErrorException) {
            return new BadGatewayException.ClientErrorException("Product Catalog API Client Error", e);
        }

        if (e instanceof HttpServerErrorException) {
            return new BadGatewayException.ServerErrorException("Product Catalog API Internal Error", e);
        }

        return new BadGatewayException("Product Catalog API Bad Gateway", e);
    }

}
