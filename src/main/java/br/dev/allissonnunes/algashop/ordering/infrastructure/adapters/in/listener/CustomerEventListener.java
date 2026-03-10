package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.listener;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerArchivedEvent;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

    private final ForConfirmCustomerRegistration forConfirmCustomerRegistration;

    private final ForAddingLoyaltyPoints forAddingLoyaltyPoints;

    @EventListener
    public void handleCustomerRegisteredEvent(final CustomerRegisteredEvent event) {
        log.info("Handling customer registered event: {}", event);
        forConfirmCustomerRegistration.confirm(event.customerId().value());
    }

    @EventListener
    public void handleCustomerArchivedEvent(final CustomerArchivedEvent event) {
        log.info("Handling customer archived event: {}", event);
    }

    @EventListener
    public void handleOrderReadyEvent(final OrderReadyEvent event) {
        log.info("Handling order ready event: {}", event);
        forAddingLoyaltyPoints.addLoyaltyPoints(event.customerId().value(), event.orderId().toString());
    }

}
