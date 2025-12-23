package com.github.allisson95.algashop.ordering.application.checkout;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BuyNowInput(
        ShippingInput shipping,
        BillingData billing,
        UUID productId,
        UUID customerId,
        Integer quantity,
        String paymentMethod
) {

}
