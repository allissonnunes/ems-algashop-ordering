package br.dev.allissonnunes.algashop.ordering.application.customer.management;

import br.dev.allissonnunes.algashop.ordering.application.AbstractApplicationIT;
import br.dev.allissonnunes.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.*;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.*;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CustomerLoyaltyPointsApplicationServiceIT extends AbstractApplicationIT {

    @Autowired
    private Customers customers;

    @Autowired
    private Orders orders;

    @Autowired
    private CustomerLoyaltyPointsApplicationService service;

    @MockitoBean
    private CustomerEventListener customerEventListener;

    @Test
    void shouldAddLoyaltyPointsToCustomer() {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        final Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();
        orders.add(order);

        service.addLoyaltyPoints(customer.getId().value(), order.getId().toString());

        final Customer retrievedCustomer = customers.ofId(customer.getId()).orElseThrow();
        assertThat(retrievedCustomer.getLoyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void shouldNotAddLoyaltyPointsToCustomerIfOrderDoesNotContainsSufficientValue() {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        final Product product = ProductTestDataBuilder.aProduct().price(new Money("99.98")).build();
        final Order order = OrderTestDataBuilder.anOrder()
                .withItem(product, new Quantity(1))
                .withItem(product, new Quantity(2))
                .status(OrderStatus.READY)
                .build();
        orders.add(order);

        service.addLoyaltyPoints(customer.getId().value(), order.getId().toString());

        final Customer retrievedCustomer = customers.ofId(customer.getId()).orElseThrow();
        assertThat(retrievedCustomer.getLoyaltyPoints()).isEqualTo(new LoyaltyPoints(0));
    }

    @Test
    void shouldThrowsExceptionIfCustomerDoesNotExist() {
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.addLoyaltyPoints(new CustomerId().value(), new OrderId().toString()));
    }

    @Test
    void shouldThrowsExceptionIfOrderDoesNotExist() {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> service.addLoyaltyPoints(customer.getId().value(), new OrderId().toString()));
    }

    @Test
    void shouldThrowsExceptionIfCustomerIsArchived() {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customer.archive();
        customers.add(customer);
        final Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();
        orders.add(order);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> service.addLoyaltyPoints(customer.getId().value(), order.getId().toString()));
    }

    @Test
    void shouldThrowsExceptionIfOrderDoesNotBelongToCustomer() {
        final Customer customer1 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer1);
        final Customer customer2 = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer2);
        final Order order = OrderTestDataBuilder.anOrder().customerId(customer2.getId()).status(OrderStatus.READY).build();
        orders.add(order);

        assertThatExceptionOfType(OrderNotBelongsToCustomerException.class)
                .isThrownBy(() -> service.addLoyaltyPoints(customer1.getId().value(), order.getId().toString()));
    }

    @ParameterizedTest
    @EnumSource(names = { "READY" }, mode = EnumSource.Mode.EXCLUDE)
    void shouldThrowsExceptionIfOrderIsNotReady(final OrderStatus orderStatus) {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        final Order order = OrderTestDataBuilder.anOrder().status(orderStatus).build();
        orders.add(order);

        assertThatExceptionOfType(CantAddLoyaltyPointsIfOrderIsNotReady.class)
                .isThrownBy(() -> service.addLoyaltyPoints(customer.getId().value(), order.getId().toString()));
    }

}