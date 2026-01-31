package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
import br.dev.allissonnunes.algashop.ordering.application.order.query.RecipientData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ShippingInput(
        @NotNull
        @Valid
        RecipientData recipient,
        @NotNull
        @Valid
        AddressData address
) {

}
