package br.dev.allissonnunes.algashop.ordering.infrastructure.listener.customer;

import br.dev.allissonnunes.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerNotificationApplicationService customerNotificationApplicationService;

    private final CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @EventListener
    public void handleCustomerRegisteredEvent(final CustomerRegisteredEvent event) {
        log.info("Handling customer registered event: {}", event);
        customerNotificationApplicationService.notifyNewRegistration(
                new NotifyNewRegistrationInput(
                        event.customerId().value(),
                        event.fullName().firstName(),
                        event.email().value()
                )
        );
    }

    @EventListener
    public void handleCustomerArchivedEvent(final CustomerArchivedEvent event) {
        log.info("Handling customer archived event: {}", event);
    }

    @EventListener
    public void handleOrderReadyEvent(final OrderReadyEvent event) {
        log.info("Handling order ready event: {}", event);
        customerLoyaltyPointsApplicationService.addLoyaltyPoints(event.customerId().value(), event.orderId().toString());
    }

}
