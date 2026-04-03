package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.BadGatewayException;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class ResilientProductCatalogClient {

    private final ProductCatalogClient productCatalogClient;

    @Cacheable(cacheNames = "algashop:product-catalog-api:v1", key = "#productId")
    @ConcurrencyLimit(10)
    @Retryable(
            maxRetries = 3L,
            delayString = "3s",
            multiplier = 2,
            includes = {
                    BadGatewayException.ServerErrorException.class,
                    GatewayTimeoutException.class,
            }
    )
    public Optional<ProductResponse> findById(final UUID productId) {
        try {
            return Optional.of(productCatalogClient.findById(productId));
        } catch (final HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (final RestClientException e) {
            if (e.getCause() instanceof SocketTimeoutException || e instanceof ResourceAccessException) {
                throw new GatewayTimeoutException("Product Catalog API Timeout", e);
            }

            if (e instanceof HttpClientErrorException) {
                throw new BadGatewayException.ClientErrorException("Product Catalog API Client Error", e);
            }

            if (e instanceof HttpServerErrorException) {
                throw new BadGatewayException.ServerErrorException("Product Catalog API Internal Error", e);
            }

            throw new BadGatewayException("Product Catalog API Bad Gateway", e);
        }
    }

}
