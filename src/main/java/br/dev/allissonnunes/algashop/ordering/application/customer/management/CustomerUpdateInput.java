package br.dev.allissonnunes.algashop.ordering.application.customer.management;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CustomerUpdateInput(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String phone,
        @NotNull
        Boolean promotionNotificationsAllowed,
        @NotNull
        @Valid
        AddressData address
) {

}
