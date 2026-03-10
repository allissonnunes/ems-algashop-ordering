package br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart;

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
