package br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record ShoppingCartOutput(
        UUID id,
        UUID customerId,
        Integer totalItems,
        BigDecimal totalAmount,
        List<ShoppingCartItemOutput> items
) {

}
