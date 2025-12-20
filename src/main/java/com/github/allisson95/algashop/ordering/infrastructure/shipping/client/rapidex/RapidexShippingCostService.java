package com.github.allisson95.algashop.ordering.infrastructure.shipping.client.rapidex;

import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
@Component
@RequiredArgsConstructor
class RapidexShippingCostService implements ShippingCostService {

    private final RapidexClient client;

    @Override
    public CalculationResponse calculate(final CalculationRequest request) {
        final DeliveryCostResponse response = client.calculate(
                new DeliveryCostRequest(
                        request.origin().value(),
                        request.destination().value()
                )
        );

        final LocalDate expectedDeliveryDate = LocalDate.now().plusDays(response.estimatedDaysToDeliver());

        return CalculationResponse.builder()
                .cost(new Money(response.deliveryCost()))
                .estimatedDeliveryDate(expectedDeliveryDate)
                .build();
    }

}
