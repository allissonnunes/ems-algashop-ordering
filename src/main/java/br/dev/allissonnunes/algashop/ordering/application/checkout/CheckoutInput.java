package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.order.query.BillingData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CheckoutInput(
        @NotNull
        UUID shoppingCartId,
        @NotBlank
        String paymentMethod,
        @NotNull
        @Valid
        ShippingInput shipping,
        @NotNull
        @Valid
        BillingData billing
) {

}
