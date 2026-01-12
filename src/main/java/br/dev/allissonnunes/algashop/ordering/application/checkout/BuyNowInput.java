package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.order.query.BillingData;
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
