package com.github.allisson95.algashop.ordering.application.checkout;

import com.github.allisson95.algashop.ordering.application.order.query.BillingData;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CheckoutInput(
        UUID shoppingCartId,
        String paymentMethod,
        ShippingInput shipping,
        BillingData billing
) {

}
