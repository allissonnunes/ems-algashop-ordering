package br.dev.allissonnunes.algashop.ordering.core.application.shipping;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShippingCostPreviewInput(@NotBlank @Size(min = 5, max = 5) String zipCode) {

}
