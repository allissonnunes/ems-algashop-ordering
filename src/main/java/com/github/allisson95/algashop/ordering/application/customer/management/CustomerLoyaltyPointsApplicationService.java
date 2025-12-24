package com.github.allisson95.algashop.ordering.application.customer.management;

import com.github.allisson95.algashop.ordering.domain.model.customer.*;
import com.github.allisson95.algashop.ordering.domain.model.order.Order;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderId;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.github.allisson95.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyPointsApplicationService {

    private final Customers customers;

    private final Orders orders;

    private final CustomerLoyaltyPointsService customerLoyaltyPointsService;

    @Transactional
    public void addLoyaltyPoints(final UUID rawCustomerId, final String rawOrderId) {
        requireNonNull(rawCustomerId, "rawCustomerId cannot be null");
        requireNonNull(rawOrderId, "rawOrderId cannot be null");

        final CustomerId customerId = new CustomerId(rawCustomerId);
        final Customer customer = customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        final OrderId orderId = new OrderId(rawOrderId);
        final Order order = orders.ofId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        customerLoyaltyPointsService.addPoints(customer, order);

        customers.add(customer);
    }

}
