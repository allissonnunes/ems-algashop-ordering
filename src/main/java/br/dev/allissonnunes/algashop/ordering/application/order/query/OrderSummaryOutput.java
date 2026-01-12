package br.dev.allissonnunes.algashop.ordering.application.order.query;

import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryOutput(
        String id,
        CustomerMinimalOutput customer,
        Integer totalItems,
        BigDecimal totalAmount,
        Instant placedAt,
        Instant paidAt,
        Instant canceledAt,
        Instant readyAt,
        String status,
        String paymentMethod
) {

    @Builder
    public OrderSummaryOutput(final Long id, final CustomerMinimalOutput customer, final Integer totalItems, final BigDecimal totalAmount, final Instant placedAt, final Instant paidAt, final Instant canceledAt, final Instant readyAt, final String status, final String paymentMethod) {
        this(new OrderId(id).toString(), customer, totalItems, totalAmount, placedAt, paidAt, canceledAt, readyAt, status, paymentMethod);
    }

}
