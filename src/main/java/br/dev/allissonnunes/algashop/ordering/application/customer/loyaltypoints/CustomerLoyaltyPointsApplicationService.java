package br.dev.allissonnunes.algashop.ordering.application.customer.loyaltypoints;

import br.dev.allissonnunes.algashop.ordering.domain.model.customer.*;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderNotFoundException;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Orders;
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
