package com.github.allisson95.algashop.ordering.application.customer.management;

import com.github.allisson95.algashop.ordering.application.commons.AddressData;
import lombok.Builder;

@Builder
public record CustomerUpdateInput(
        String firstName,
        String lastName,
        String phone,
        Boolean promotionNotificationsAllowed,
        AddressData address
) {

}
