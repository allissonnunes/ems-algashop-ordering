package br.dev.allissonnunes.algashop.ordering.core.application.checkout;

import br.dev.allissonnunes.algashop.ordering.core.application.utility.Mapper;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.Address;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.Recipient;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.Shipping;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.ShippingInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ShippingInputDisassembler {

    private final Mapper mapper;

    public Shipping toDomainModel(final ShippingInput shipping, final ShippingCostService.CalculationResponse shippingCostDetails) {
        return Shipping.builder()
                .cost(shippingCostDetails.cost())
                .expectedDeliveryDate(shippingCostDetails.estimatedDeliveryDate())
                .recipient(mapper.convert(shipping.recipient(), Recipient.class))
                .address(mapper.convert(shipping.address(), Address.class))
                .build();
    }

}
