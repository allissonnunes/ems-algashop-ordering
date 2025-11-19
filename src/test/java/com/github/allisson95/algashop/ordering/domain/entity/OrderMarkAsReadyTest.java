package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

class OrderMarkAsReadyTest {

    @Test
    void givenAPaidOrder_whenMarkAsReady_shouldUpdateStatus() {
        final Order paidOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();

        assertThatCode(paidOrder::markAsReady).doesNotThrowAnyException();

        assertWith(paidOrder,
                o -> assertThat(o.isReady()).isTrue(),
                o -> assertThat(o.paidAt()).isNotNull(),
                o -> assertThatTemporal(o.paidAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS))
        );
    }

    @ParameterizedTest
    @EnumSource(names = { "PAID", "READY" }, mode = EnumSource.Mode.EXCLUDE)
    void givenANonPaidOrder_whenMarkAsReady_shouldThrowException(final OrderStatus nonPaidStatus) {
        final Order nonPaidOrder = OrderTestDataBuilder.anOrder().status(nonPaidStatus).build();

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(nonPaidOrder::markAsReady)
                .withMessage("Cannot change order %s status from %s to %s".formatted(nonPaidOrder.id(), nonPaidOrder.status(), OrderStatus.READY));
    }

}
