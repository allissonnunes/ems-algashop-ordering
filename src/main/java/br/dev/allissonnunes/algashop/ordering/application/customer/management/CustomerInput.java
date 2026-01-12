package br.dev.allissonnunes.algashop.ordering.application.customer.management;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerInput(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotNull
        @Past
        LocalDate birthDate,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String phone,
        @NotBlank
        String document,
        @NotNull
        Boolean promotionNotificationsAllowed,
        @NotNull
        @Valid
        AddressData address
) {

}
