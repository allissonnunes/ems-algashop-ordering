package com.github.allisson95.algashop.ordering.application.order.query;

import com.github.allisson95.algashop.ordering.application.commons.AddressData;
import lombok.Builder;

@Builder
public record BillingData(
        String firstName,
        String lastName,
        String document,
        String phone,
        String email,
        AddressData address
) {

}
