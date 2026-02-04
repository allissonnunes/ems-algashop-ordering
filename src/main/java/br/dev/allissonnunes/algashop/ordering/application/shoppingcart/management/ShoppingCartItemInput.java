package br.dev.allissonnunes.algashop.ordering.application.shoppingcart.management;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record ShoppingCartItemInput(
        @NotNull
        @Positive
        Integer quantity,
        @NotNull
        UUID productId,
        UUID shoppingCartId
) {

}
