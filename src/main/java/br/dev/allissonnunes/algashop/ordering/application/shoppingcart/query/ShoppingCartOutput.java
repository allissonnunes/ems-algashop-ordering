package br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query;

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
