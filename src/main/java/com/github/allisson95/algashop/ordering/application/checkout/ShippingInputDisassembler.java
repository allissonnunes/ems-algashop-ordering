package com.github.allisson95.algashop.ordering.application.checkout;

import com.github.allisson95.algashop.ordering.application.utility.Mapper;
import com.github.allisson95.algashop.ordering.domain.model.commons.Address;
import com.github.allisson95.algashop.ordering.domain.model.commons.Document;
import com.github.allisson95.algashop.ordering.domain.model.commons.FullName;
import com.github.allisson95.algashop.ordering.domain.model.commons.Phone;
import com.github.allisson95.algashop.ordering.domain.model.order.Recipient;
import com.github.allisson95.algashop.ordering.domain.model.order.Shipping;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShippingInputDisassembler {

    private final Mapper mapper;

    public Shipping toDomainModel(final ShippingInput shipping, final CalculationResponse shippingCostDetails) {
        return Shipping.builder()
                .cost(shippingCostDetails.cost())
                .expectedDeliveryDate(shippingCostDetails.estimatedDeliveryDate())
                .recipient(toDomainModel(shipping.recipient()))
                .address(mapper.convert(shipping.address(), Address.class))
                .build();
    }

    private Recipient toDomainModel(final RecipientData recipient) {
        return Recipient.builder()
                .fullName(new FullName(recipient.firstName(), recipient.lastName()))
                .document(new Document(recipient.document()))
                .phone(new Phone(recipient.phone()))
                .build();
    }

}
