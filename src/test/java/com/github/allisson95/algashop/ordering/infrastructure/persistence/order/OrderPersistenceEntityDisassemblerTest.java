package com.github.allisson95.algashop.ordering.infrastructure.persistence.order;

import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.order.Order;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderId;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderStatus;
import com.github.allisson95.algashop.ordering.domain.model.order.PaymentMethod;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderPersistenceEntityDisassemblerTest {

    private final OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

    @Test
    void toDomainEntity() {
        final OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        final Order domainEntity = this.disassembler.toDomainEntity(entity);

        assertWith(domainEntity,
                e -> assertThat(e.id()).isEqualTo(new OrderId(entity.getId())),
                e -> assertThat(e.customerId()).isEqualTo(new CustomerId(entity.getCustomerId())),
                e -> assertThat(e.totalAmount()).isEqualTo(new Money(entity.getTotalAmount())),
                e -> assertThat(e.totalItems()).isEqualTo(new Quantity(entity.getTotalItems())),
                e -> assertThat(e.placedAt()).isEqualTo(entity.getPlacedAt()),
                e -> assertThat(e.paidAt()).isEqualTo(entity.getPaidAt()),
                e -> assertThat(e.cancelledAt()).isEqualTo(entity.getCancelledAt()),
                e -> assertThat(e.readyAt()).isEqualTo(entity.getReadyAt()),
                e -> assertThat(e.status()).isEqualTo(OrderStatus.valueOf(entity.getStatus())),
                e -> assertThat(e.paymentMethod()).isEqualTo(PaymentMethod.valueOf(entity.getPaymentMethod())),
                e -> assertThat(e.items()).hasSize(2)
        );
    }

}