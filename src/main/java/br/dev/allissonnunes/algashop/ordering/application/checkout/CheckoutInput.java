package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.order.query.BillingData;
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
