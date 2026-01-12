package br.dev.allissonnunes.algashop.ordering.application.shoppingcart.management;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ShoppingCartItemInput(
        Integer quantity,
        UUID productId,
        UUID shoppingCartId
) {

}
