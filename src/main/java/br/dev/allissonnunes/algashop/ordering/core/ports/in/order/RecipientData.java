package br.dev.allissonnunes.algashop.ordering.core.ports.in.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RecipientData(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String document,
        @NotBlank
        String phone
) {

}
