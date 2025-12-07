package com.github.allisson95.algashop.ordering.infrastructure.persistence.assembler;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderPersistenceEntityAssemblerTest {

    private final OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();

    @Test
    void fromDomain() {
        final Order order = OrderTestDataBuilder.anOrder().build();

        final OrderPersistenceEntity entity = assembler.fromDomain(order);

        assertWith(entity,
                e -> assertThat(e.getId()).isEqualTo(order.id().value().toLong()),
                e -> assertThat(e.getCustomerId()).isEqualTo(order.customerId().value()),
                e -> assertThat(e.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                e -> assertThat(e.getTotalItems()).isEqualTo(order.totalItems().value()),
                e -> assertThat(e.getPlacedAt()).isEqualTo(order.placedAt()),
                e -> assertThat(e.getPaidAt()).isEqualTo(order.paidAt()),
                e -> assertThat(e.getCancelledAt()).isEqualTo(order.cancelledAt()),
                e -> assertThat(e.getReadyAt()).isEqualTo(order.readyAt()),
                e -> assertThat(e.getStatus()).isEqualTo(order.status().name()),
                e -> assertThat(e.getPaymentMethod()).isEqualTo(order.paymentMethod().name())
        );
    }

    @Test
    void merge() {
    }

}