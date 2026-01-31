package br.dev.allissonnunes.algashop.ordering.application.order.query;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BillingData(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String document,
        @NotBlank
        String phone,
        @NotBlank
        String email,
        @NotNull
        @Valid
        AddressData address
) {

}
