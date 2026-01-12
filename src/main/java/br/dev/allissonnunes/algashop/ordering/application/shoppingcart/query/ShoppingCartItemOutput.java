package br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ShoppingCartItemOutput(
        UUID id,
        UUID productId,
        String name,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalAmount,
        Boolean available
) {

}
