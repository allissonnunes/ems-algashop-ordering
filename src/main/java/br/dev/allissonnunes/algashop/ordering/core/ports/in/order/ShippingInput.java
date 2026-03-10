package br.dev.allissonnunes.algashop.ordering.core.ports.in.order;

import br.dev.allissonnunes.algashop.ordering.core.ports.commons.AddressData;
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
