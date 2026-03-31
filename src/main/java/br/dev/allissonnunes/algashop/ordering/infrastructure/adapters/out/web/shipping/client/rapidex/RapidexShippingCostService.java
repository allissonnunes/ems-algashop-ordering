package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.BadGatewayException;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.time.LocalDate;

@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
@Component
@RequiredArgsConstructor
class RapidexShippingCostService implements ShippingCostService {

    private final RapidexClient client;

    @Override
    public CalculationResponse calculate(final CalculationRequest request) {
        final DeliveryCostResponse response;
        try {
            response = client.calculate(
                    new DeliveryCostRequest(
                            request.origin().value(),
                            request.destination().value()
                    )
            );
        } catch (final ResourceAccessException e) {
            throw new GatewayTimeoutException("Rapidex API Timeout", e);
        } catch (final RestClientException e) {
            if (e.getCause() instanceof SocketTimeoutException timeoutException) {
                throw new GatewayTimeoutException("Rapidex API Timeout", timeoutException);
            }
            throw new BadGatewayException("Rapidex API Bad Gateway", e);
        }

        final LocalDate expectedDeliveryDate = LocalDate.now().plusDays(response.estimatedDaysToDeliver());

        return CalculationResponse.builder()
                .cost(new Money(response.deliveryCost()))
                .estimatedDeliveryDate(expectedDeliveryDate)
                .build();
    }

}
