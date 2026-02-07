package br.dev.allissonnunes.algashop.ordering.application.order.query;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderStatus;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Orders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(DataJpaCleanUpExtension.class)
class OrderQueryServiceIT {

    @Autowired
    private Customers customers;

    @Autowired
    private Orders orders;

    @Autowired
    private OrderQueryService orderQueryService;

    @Test
    void shouldFindOrderById() {
        final var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        final var order = OrderTestDataBuilder.anOrder().customerId(customer.getId()).build();
        orders.add(order);

        final OrderDetailOutput orderDetailOutput = orderQueryService.findById(order.getId().toString());

        assertThat(orderDetailOutput).isNotNull();
        assertThat(orderDetailOutput.id()).isEqualTo(order.getId().toString());
        assertThat(orderDetailOutput.customer().id()).isEqualTo(customer.getId().value());
    }

    @Test
    void shouldFilterByPage() {
        final var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).withItems(false).customerId(customer.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).customerId(customer.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).customerId(customer.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.READY).customerId(customer.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).customerId(customer.getId()).build());

        final Page<OrderSummaryOutput> orderSummaryOutputPage = orderQueryService.filter(new OrderFilter(0, 3));

        assertThat(orderSummaryOutputPage).isNotNull();
        assertThat(orderSummaryOutputPage.getTotalPages()).isEqualTo(2);
        assertThat(orderSummaryOutputPage.getTotalElements()).isEqualTo(5);
        assertThat(orderSummaryOutputPage.getNumberOfElements()).isEqualTo(3);
        assertThat(orderSummaryOutputPage.getContent()).hasSize(3);
    }

    @Test
    public void shouldFilterByCustomerId() {
        Customer customer1 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer1);

        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).withItems(false).customerId(customer1.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).customerId(customer1.getId()).build());

        Customer customer2 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer2);
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).customerId(customer2.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.READY).customerId(customer2.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).customerId(customer2.getId()).build());

        OrderFilter filter = new OrderFilter();
        filter.setCustomerId(customer1.getId().value());

        Page<OrderSummaryOutput> page = orderQueryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByMultipleParams() {
        Customer customer1 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer1);

        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).withItems(false).customerId(customer1.getId()).build());
        Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).customerId(customer1.getId()).build();
        orders.add(order1);

        Customer customer2 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer2);
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).customerId(customer2.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.READY).customerId(customer2.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).customerId(customer2.getId()).build());

        OrderFilter filter = new OrderFilter();
        filter.setCustomerId(customer1.getId().value());
        filter.setStatus(OrderStatus.PLACED.toString().toLowerCase());
        filter.setTotalAmountFrom(order1.getTotalAmount().value());

        Page<OrderSummaryOutput> page = orderQueryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void givenInvalidOrderId_whenFilter_shouldReturnEmptyPage() {
        Customer customer1 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer1);

        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).withItems(false).customerId(customer1.getId()).build());
        Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).customerId(customer1.getId()).build();
        orders.add(order1);

        Customer customer2 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer2);
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).customerId(customer2.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.READY).customerId(customer2.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).customerId(customer2.getId()).build());

        OrderFilter filter = new OrderFilter();
        filter.setOrderId("ABC");

        Page<OrderSummaryOutput> page = orderQueryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getNumberOfElements()).isEqualTo(0);
    }

    @Test
    public void shouldOrderByStatus() {
        Customer customer1 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer1);

        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).withItems(false).customerId(customer1.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).customerId(customer1.getId()).build());

        Customer customer2 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer2);
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).customerId(customer2.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.READY).customerId(customer2.getId()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).customerId(customer2.getId()).build());

        OrderFilter filter = new OrderFilter();
        filter.setSortByProperty(OrderFilter.SortType.STATUS);
        filter.setSortDirection(Sort.Direction.ASC);

        Page<OrderSummaryOutput> page = orderQueryService.filter(filter);

        assertThat(page.getContent().getFirst().status()).isEqualTo(OrderStatus.CANCELED.toString());
    }

}