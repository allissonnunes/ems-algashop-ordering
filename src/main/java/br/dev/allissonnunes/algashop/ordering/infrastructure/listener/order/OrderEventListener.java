package br.dev.allissonnunes.algashop.ordering.infrastructure.listener.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderCanceledEvent;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderPaidEvent;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderPlacedEvent;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    @EventListener
    public void handleOrderPlacedEvent(final OrderPlacedEvent event) {
        log.info("Order placed: {}", event.orderId());
    }

    @EventListener
    public void handleOrderPaidEvent(final OrderPaidEvent event) {
        log.info("Order paid: {}", event.orderId());
    }

    @EventListener
    public void handleOrderReadyEvent(final OrderReadyEvent event) {
        log.info("Order ready: {}", event.orderId());
    }

    @EventListener
    public void handleOrderCanceledEvent(final OrderCanceledEvent event) {
        log.info("Order canceled: {}", event.orderId());
    }

}
