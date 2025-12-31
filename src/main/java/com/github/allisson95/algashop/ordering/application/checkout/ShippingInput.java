package com.github.allisson95.algashop.ordering.application.checkout;

import com.github.allisson95.algashop.ordering.application.commons.AddressData;
import com.github.allisson95.algashop.ordering.application.order.query.RecipientData;
import lombok.Builder;

@Builder
public record ShippingInput(
        RecipientData recipient,
        AddressData address
) {

}
