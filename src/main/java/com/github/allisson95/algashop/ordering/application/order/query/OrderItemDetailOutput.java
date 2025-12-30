package com.github.allisson95.algashop.ordering.application.order.query;

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
