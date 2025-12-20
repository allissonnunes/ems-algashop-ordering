package com.github.allisson95.algashop.ordering.infrastructure.fake;

import com.github.allisson95.algashop.ordering.domain.model.service.ShippingCostService;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Money;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "FAKE")
@Component
class FakeShippingCostService implements ShippingCostService {

    @Override
    public CalculationResponse calculate(final CalculationRequest request) {
        return new CalculationResponse(
                new Money("20"),
                LocalDate.now().plusDays(5)
        );
    }

}
