package br.dev.allissonnunes.algashop.ordering.core.application.order.query;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderDetailOutput(
        String id,
        CustomerMinimalOutput customer,
        Integer totalItems,
        BigDecimal totalAmount,
        Instant placedAt,
        Instant paidAt,
        Instant canceledAt,
        Instant readyAt,
        String status,
        String paymentMethod,
        ShippingData shipping,
        BillingData billing,
        List<OrderItemDetailOutput> items
) {

}
