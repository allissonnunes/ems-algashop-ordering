package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.order.query.BillingData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BuyNowInput(
        @NotNull
        @Valid
        ShippingInput shipping,
        @NotNull
        @Valid
        BillingData billing,
        @NotNull
        UUID productId,
        @NotNull
        UUID customerId,
        @NotNull
        @Positive
        Integer quantity,
        @NotBlank
        String paymentMethod
) {

}
