package com.github.allisson95.algashop.ordering.application.order.query;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customers;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.order.Orders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}