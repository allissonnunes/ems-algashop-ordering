package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

class OrderCancelTest {

    @ParameterizedTest
    @EnumSource(names = { "CANCELED" }, mode = EnumSource.Mode.EXCLUDE)
    void givenUncanceledOrder_whenCancel_shouldSetCanceledStatus(final OrderStatus uncanceledStatus) {
        final Order order = OrderTestDataBuilder.anOrder().status(uncanceledStatus).build();

        assertThatCode(order::cancel).doesNotThrowAnyException();
        assertWith(order,
                o -> assertThat(o.isCanceled()).isTrue(),
                o -> assertThatTemporal(o.cancelledAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS))
        );
    }

    @Test
    void givenACanceledOrder_whenCancelAgain_thenThrowException() {
        final Order canceledOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(canceledOrder::cancel)
                .withMessage("Cannot change order %s status from %s to %s".formatted(canceledOrder.id(), canceledOrder.status(), OrderStatus.CANCELED));
    }

}
