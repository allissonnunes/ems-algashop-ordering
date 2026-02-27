package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.AbstractDomainIT;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class OrdersIT extends AbstractDomainIT {

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

    @BeforeEach
    void setUp() {
        customers.add(CustomerTestDataBuilder.existingCustomer().build());
    }

    @Test
    void shouldPersistAndFind() {
        final Order order = OrderTestDataBuilder.anOrder().build();

        orders.add(order);

        final Optional<Order> possibleOrder = orders.ofId(order.getId());
        assertThat(possibleOrder).isPresent();

        assertWith(possibleOrder.get(),
                o -> assertThat(o.getId()).isEqualTo(order.getId()),
                o -> assertThat(o.getCustomerId()).isEqualTo(order.getCustomerId()),
                o -> assertThat(o.getTotalAmount()).isEqualTo(order.getTotalAmount()),
                o -> assertThat(o.getTotalItems()).isEqualTo(order.getTotalItems()),
                o -> assertThat(o.getPlacedAt()).isEqualTo(order.getPlacedAt()),
                o -> assertThat(o.getPaidAt()).isEqualTo(order.getPaidAt()),
                o -> assertThat(o.getCanceledAt()).isEqualTo(order.getCanceledAt()),
                o -> assertThat(o.getReadyAt()).isEqualTo(order.getReadyAt()),
                o -> assertThat(o.getBilling()).isEqualTo(order.getBilling()),
                o -> assertThat(o.getShipping()).isEqualTo(order.getShipping()),
                o -> assertThat(o.getStatus()).isEqualTo(order.getStatus()),
                o -> assertThat(o.getPaymentMethod()).isEqualTo(order.getPaymentMethod())
        );
    }

    @Test
    void shouldUpdateExistingOrder() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        orders.add(order);

        order = orders.ofId(order.getId()).orElseThrow();
        order.markAsPaid();

        orders.add(order);

        order = orders.ofId(order.getId()).orElseThrow();

        assertThat(order.isPaid()).isTrue();
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        orders.add(order);

        Order orderT1 = orders.ofId(order.getId()).orElseThrow();
        Order orderT2 = orders.ofId(order.getId()).orElseThrow();

        orderT1.markAsPaid();
        orders.add(orderT1);

        orderT2.cancel();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> orders.add(orderT2));

        Order savedOrder = orders.ofId(order.getId()).orElseThrow();

        assertThat(savedOrder.getCanceledAt()).isNull();
        assertThat(savedOrder.getPaidAt()).isNotNull();
    }

    @Test
    void shouldCountExistingOrders() {
        assertThat(orders.count()).isZero();

        Order order = OrderTestDataBuilder.anOrder().build();
        orders.add(order);

        assertThat(orders.count()).isEqualTo(1);
    }

    @Test
    void shouldReturnIfOrderExists() {
        final Order order = OrderTestDataBuilder.anOrder().build();
        orders.add(order);

        assertThat(orders.exists(order.getId())).isTrue();
        assertThat(orders.exists(new OrderId())).isFalse();
    }

    @Test
    void shouldListExistingOrdersByYear() {
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());

        final CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        final List<Order> yearlyOrders = orders.placedByCustomerInYear(customerId, Year.now());

        assertThat(yearlyOrders).hasSize(2);
    }

    @Test
    void shouldListExistingPlacedOrdersByCustomerInYear() {
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).build());

        final CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        List<Order> yearlyOrders = orders.placedByCustomerInYear(customerId, Year.now());

        assertThat(yearlyOrders).hasSize(2);

        yearlyOrders = orders.placedByCustomerInYear(customerId, Year.now().minusYears(1L));

        assertThat(yearlyOrders).isEmpty();

        yearlyOrders = orders.placedByCustomerInYear(new CustomerId(), Year.now());

        assertThat(yearlyOrders).isEmpty();
    }

    @Test
    void shouldReturnSalesQuantityByCustomerInYear() {
        final Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        final Order order2 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();

        orders.add(order1);
        orders.add(order2);
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());

        final CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        final long salesQuantity = orders.salesQuantityByCustomerInYear(customerId, Year.now());

        assertThat(salesQuantity).isEqualTo(2L);
        assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now().minusYears(1L))).isZero();
    }

    @Test
    void shouldReturnTotalSoldByCustomer() {
        final Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        final Order order2 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();

        orders.add(order1);
        orders.add(order2);
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());

        final Money expectedTotalAmount = order1.getTotalAmount().add(order2.getTotalAmount());
        final CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Money totalSold = orders.totalSoldByCustomer(customerId);

        assertThat(totalSold).isEqualTo(expectedTotalAmount);
        assertThat(orders.totalSoldByCustomer(new CustomerId())).isEqualTo(Money.ZERO);
    }

}