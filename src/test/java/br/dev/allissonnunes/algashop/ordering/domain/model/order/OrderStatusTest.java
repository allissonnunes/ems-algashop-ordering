package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void canBeUpdatedTo() {
        assertThat(OrderStatus.DRAFT.canBeUpdatedTo(OrderStatus.PLACED)).isTrue();
        assertThat(OrderStatus.DRAFT.canBeUpdatedTo(OrderStatus.CANCELED)).isTrue();
        assertThat(OrderStatus.PLACED.canBeUpdatedTo(OrderStatus.PAID)).isTrue();
        assertThat(OrderStatus.PLACED.canBeUpdatedTo(OrderStatus.CANCELED)).isTrue();
        assertThat(OrderStatus.PAID.canBeUpdatedTo(OrderStatus.READY)).isTrue();
        assertThat(OrderStatus.PAID.canBeUpdatedTo(OrderStatus.CANCELED)).isTrue();
        assertThat(OrderStatus.READY.canBeUpdatedTo(OrderStatus.CANCELED)).isTrue();

        assertThat(OrderStatus.CANCELED.canBeUpdatedTo(OrderStatus.DRAFT)).isFalse();
        assertThat(OrderStatus.CANCELED.canBeUpdatedTo(OrderStatus.PLACED)).isFalse();
        assertThat(OrderStatus.CANCELED.canBeUpdatedTo(OrderStatus.PAID)).isFalse();
        assertThat(OrderStatus.CANCELED.canBeUpdatedTo(OrderStatus.READY)).isFalse();
    }

    @Test
    void cantBeUpdatedTo() {
        assertThat(OrderStatus.CANCELED.cantBeUpdatedTo(OrderStatus.DRAFT)).isTrue();
        assertThat(OrderStatus.CANCELED.cantBeUpdatedTo(OrderStatus.PLACED)).isTrue();
        assertThat(OrderStatus.CANCELED.cantBeUpdatedTo(OrderStatus.PAID)).isTrue();
        assertThat(OrderStatus.CANCELED.cantBeUpdatedTo(OrderStatus.READY)).isTrue();
    }

}