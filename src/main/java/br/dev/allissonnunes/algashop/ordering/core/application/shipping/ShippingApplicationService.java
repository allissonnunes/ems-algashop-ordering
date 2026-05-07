package br.dev.allissonnunes.algashop.ordering.core.application.shipping;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.Address;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.ZipCode;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingApplicationService {

    private final OriginAddressService originAddressService;

    private final ShippingCostService shippingCostService;

    public ShippingCostPreviewOutput previewCost(ShippingCostPreviewInput input) {
        final Address originAddress = originAddressService.originAddress();

        var request = ShippingCostService.CalculationRequest.builder()
                .origin(originAddress.zipCode())
                .destination(new ZipCode(input.zipCode()))
                .build();

        var result = shippingCostService.calculate(request);

        return new ShippingCostPreviewOutput(result.cost().value(), result.estimatedDeliveryDate());
    }

}
