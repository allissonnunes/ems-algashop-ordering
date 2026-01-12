package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIsReadyTest {

    @Test
    void givenOrderIsReady_whenIsReady_thenReturnTrue() {
        final Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();
        assertThat(order.isReady()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(names = { "READY" }, mode = EnumSource.Mode.EXCLUDE)
    void givenOrderIsNotReady_whenIsReady_thenReturnFalse(final OrderStatus status) {
        final Order order = OrderTestDataBuilder.anOrder().status(status).build();
        assertThat(order.isReady()).isFalse();
    }

}
