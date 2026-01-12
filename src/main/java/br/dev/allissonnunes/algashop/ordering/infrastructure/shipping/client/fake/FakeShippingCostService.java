package br.dev.allissonnunes.algashop.ordering.infrastructure.shipping.client.fake;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
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
