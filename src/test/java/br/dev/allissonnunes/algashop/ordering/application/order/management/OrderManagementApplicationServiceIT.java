package br.dev.allissonnunes.algashop.ordering.application.order.management;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.*;
import br.dev.allissonnunes.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(DataJpaCleanUpExtension.class)
class OrderManagementApplicationServiceIT {

    @Autowired
    private Orders orders;

    @Autowired
    private OrderManagementApplicationService orderManagementApplicationService;

    @Autowired
    private Customers customers;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldCancelAnOrder() {
        final Order order = OrderTestDataBuilder.anOrder().build();
        final OrderId orderId = order.getId();
        orders.add(order);
        assertThat(orders.ofId(orderId).orElseThrow().isCanceled()).isFalse();

        orderManagementApplicationService.cancel(orderId.value().toLong());

        assertWith(orders.ofId(orderId).orElseThrow(),
                o -> assertThat(o.isCanceled()).isTrue(),
                o -> assertThat(o.getCanceledAt()).isNotNull()
        );

        verify(orderEventListener, times(1)).handleOrderCanceledEvent(any(OrderCanceledEvent.class));
    }

    @Test
    void shouldThrowExceptionIfCancelAnInexistentOrder() {
        assertThatExceptionOfType(OrderNotFoundException.class).isThrownBy(
                () -> orderManagementApplicationService.cancel(new OrderId().value().toLong()));
    }

    @Test
    void shouldThrowExceptionIfCancelAnOrderThatIsAlreadyCanceled() {
        final Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();
        final OrderId orderId = order.getId();
        orders.add(order);
        assertThat(orders.ofId(orderId).orElseThrow().isCanceled()).isTrue();

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class).isThrownBy(
                () -> orderManagementApplicationService.cancel(orderId.value().toLong()));
    }

    @Test
    void shouldMarkAsPaid() {
        final Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        final OrderId orderId = order.getId();
        orders.add(order);
        assertThat(orders.ofId(orderId).orElseThrow().isCanceled()).isFalse();

        orderManagementApplicationService.markAsPaid(orderId.value().toLong());

        assertWith(orders.ofId(orderId).orElseThrow(),
                o -> assertThat(o.isPaid()).isTrue(),
                o -> assertThat(o.getPaidAt()).isNotNull()
        );

        verify(orderEventListener, times(1)).handleOrderPaidEvent(any(OrderPaidEvent.class));
    }

    @Test
    void shouldThrowExceptionIfTryToMarkAsPaidAnOrderThatDoesNotExists() {
        assertThatExceptionOfType(OrderNotFoundException.class).isThrownBy(
                () -> orderManagementApplicationService.markAsPaid(new OrderId().value().toLong()));
    }

    @ParameterizedTest
    @EnumSource(names = { "DRAFT", "PAID", "CANCELED" }, mode = EnumSource.Mode.INCLUDE)
    void shouldThrowExceptionIfTryToMarkAsPaidAnOrderThatDoesNotBeMarkedAsPaid(final OrderStatus orderStatus) {
        final Order order = OrderTestDataBuilder.anOrder().status(orderStatus).build();
        final OrderId orderId = order.getId();
        orders.add(order);

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class).isThrownBy(
                () -> orderManagementApplicationService.markAsPaid(orderId.value().toLong()));
    }

    @Test
    void shouldMarkAsReady() {
        final Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        final OrderId orderId = order.getId();
        orders.add(order);

        orderManagementApplicationService.markAsReady(orderId.value().toLong());

        assertWith(orders.ofId(orderId).orElseThrow(),
                o -> assertThat(o.isReady()).isTrue(),
                o -> assertThat(o.getReadyAt()).isNotNull()
        );

        verify(orderEventListener, times(1)).handleOrderReadyEvent(any(OrderReadyEvent.class));
    }

    @Test
    void shouldThrowExceptionIfTryToMarkAsReadyAnOrderThatDoesNotExists() {
        assertThatExceptionOfType(OrderNotFoundException.class).isThrownBy(
                () -> orderManagementApplicationService.markAsReady(new OrderId().value().toLong()));
    }

    @ParameterizedTest
    @EnumSource(names = { "DRAFT", "PLACED", "READY", "CANCELED" }, mode = EnumSource.Mode.INCLUDE)
    void shouldThrowExceptionIfTryToMarkAsReadyAnOrderThatDoesNotBeMarkedAsPaid(final OrderStatus orderStatus) {
        final Order order = OrderTestDataBuilder.anOrder().status(orderStatus).build();
        final OrderId orderId = order.getId();
        orders.add(order);

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class).isThrownBy(
                () -> orderManagementApplicationService.markAsReady(orderId.value().toLong()));
    }

}