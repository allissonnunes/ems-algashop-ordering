package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderStatus;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.PaymentMethod;
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
                e -> assertThat(e.getId()).isEqualTo(new OrderId(entity.getId())),
                e -> assertThat(e.getCustomerId()).isEqualTo(new CustomerId(entity.getCustomerId())),
                e -> assertThat(e.getTotalAmount()).isEqualTo(new Money(entity.getTotalAmount())),
                e -> assertThat(e.getTotalItems()).isEqualTo(new Quantity(entity.getTotalItems())),
                e -> assertThat(e.getPlacedAt()).isEqualTo(entity.getPlacedAt()),
                e -> assertThat(e.getPaidAt()).isEqualTo(entity.getPaidAt()),
                e -> assertThat(e.getCanceledAt()).isEqualTo(entity.getCanceledAt()),
                e -> assertThat(e.getReadyAt()).isEqualTo(entity.getReadyAt()),
                e -> assertThat(e.getStatus()).isEqualTo(OrderStatus.valueOf(entity.getStatus())),
                e -> assertThat(e.getPaymentMethod()).isEqualTo(PaymentMethod.valueOf(entity.getPaymentMethod())),
                e -> assertThat(e.getItems()).hasSize(2)
        );
    }

}