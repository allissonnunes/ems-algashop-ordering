package br.dev.allissonnunes.algashop.ordering.core.application.order;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.Orders;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.ForManagingOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class OrderManagementApplicationService implements ForManagingOrders {

    private final Orders orders;

    @Transactional
    @Override
    public void cancel(final Long rawOrderId) {
        requireNonNull(rawOrderId, "rawOrderId must not be null");

        final OrderId orderId = new OrderId(rawOrderId);
        final Order order = orders.ofId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.cancel();

        orders.add(order);
    }

    @Transactional
    @Override
    public void markAsPaid(final Long rawOrderId) {
        requireNonNull(rawOrderId, "rawOrderId must not be null");

        final OrderId orderId = new OrderId(rawOrderId);
        final Order order = orders.ofId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.markAsPaid();

        orders.add(order);
    }

    @Transactional
    @Override
    public void markAsReady(final Long rawOrderId) {
        requireNonNull(rawOrderId, "rawOrderId must not be null");

        final OrderId orderId = new OrderId(rawOrderId);
        final Order order = orders.ofId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.markAsReady();

        orders.add(order);
    }

}
