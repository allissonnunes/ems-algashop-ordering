package com.github.allisson95.algashop.ordering.domain.model.repository;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.entity.OrderStatus;
import com.github.allisson95.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.configuration.SpringDataJpaConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Import(SpringDataJpaConfiguration.class)
@DataJpaTest(showSql = false, includeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.github.allisson95.algashop.ordering.infrastructure.persistence.provider.*PersistenceProvider"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.github.allisson95.algashop.ordering.infrastructure.persistence.assembler.*PersistenceEntityAssembler"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.github.allisson95.algashop.ordering.infrastructure.persistence.disassembler.*PersistenceEntityDisassembler"),
})
class OrdersIT {

    @Autowired
    private Orders orders;

    @Test
    void shouldPersistAndFind() {
        final Order order = OrderTestDataBuilder.anOrder().build();

        orders.add(order);

        final Optional<Order> possibleOrder = orders.ofId(order.id());
        assertThat(possibleOrder).isPresent();

        assertWith(possibleOrder.get(),
                o -> assertThat(o.id()).isEqualTo(order.id()),
                o -> assertThat(o.customerId()).isEqualTo(order.customerId()),
                o -> assertThat(o.totalAmount()).isEqualTo(order.totalAmount()),
                o -> assertThat(o.totalItems()).isEqualTo(order.totalItems()),
                o -> assertThat(o.placedAt()).isEqualTo(order.placedAt()),
                o -> assertThat(o.paidAt()).isEqualTo(order.paidAt()),
                o -> assertThat(o.cancelledAt()).isEqualTo(order.cancelledAt()),
                o -> assertThat(o.readyAt()).isEqualTo(order.readyAt()),
//                o -> assertThat(o.billing()).isEqualTo(order.billing()),
//                o -> assertThat(o.shipping()).isEqualTo(order.shipping()),
                o -> assertThat(o.status()).isEqualTo(order.status()),
                o -> assertThat(o.paymentMethod()).isEqualTo(order.paymentMethod())
        );
    }

    @Test
    void shouldUpdateExistingOrder() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();
        order.markAsPaid();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        assertThat(order.isPaid()).isTrue();
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        orders.add(order);

        Order orderT1 = orders.ofId(order.id()).orElseThrow();
        Order orderT2 = orders.ofId(order.id()).orElseThrow();

        orderT1.markAsPaid();
        orders.add(orderT1);

        orderT2.cancel();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> orders.add(orderT2));

        Order savedOrder = orders.ofId(order.id()).orElseThrow();

        assertThat(savedOrder.cancelledAt()).isNull();
        assertThat(savedOrder.paidAt()).isNotNull();
    }

}