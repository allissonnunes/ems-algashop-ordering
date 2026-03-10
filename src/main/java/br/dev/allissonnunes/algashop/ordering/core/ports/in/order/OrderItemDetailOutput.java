package br.dev.allissonnunes.algashop.ordering.core.ports.in.order;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemDetailOutput(
        String id,
        String orderId,
        UUID productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalAmount
) {

}
