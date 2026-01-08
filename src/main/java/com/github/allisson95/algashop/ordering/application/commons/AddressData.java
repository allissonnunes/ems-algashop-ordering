package com.github.allisson95.algashop.ordering.application.commons;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddressData(
        @NotBlank
        String street,
        @NotBlank
        String number,
        String complement,
        @NotBlank
        String neighborhood,
        @NotBlank
        String city,
        @NotBlank
        String state,
        @NotBlank
        String zipCode
) {

}
