package com.github.allisson95.algashop.ordering.application.order.query;

import com.github.allisson95.algashop.ordering.application.checkout.RecipientData;
import com.github.allisson95.algashop.ordering.application.commons.AddressData;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ShippingData(
        BigDecimal cost,
        LocalDate expectedDeliveryDate,
        RecipientData recipient,
        AddressData address
) {

}
