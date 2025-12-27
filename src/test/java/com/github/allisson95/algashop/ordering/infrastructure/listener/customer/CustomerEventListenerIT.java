package com.github.allisson95.algashop.ordering.infrastructure.listener.customer;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.github.allisson95.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.github.allisson95.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import com.github.allisson95.algashop.ordering.domain.model.commons.Email;
import com.github.allisson95.algashop.ordering.domain.model.commons.FullName;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderId;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderReadyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(DataJpaCleanUpExtension.class)
class CustomerEventListenerIT {

    @Autowired
    private ApplicationEventPublisher publisher;

    @MockitoBean
    private CustomerNotificationApplicationService customerNotificationApplicationService;

    @MockitoBean
    private CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @Test
    void shouldHandleCustomerRegisteredEventEvent() {
        final CustomerRegisteredEvent event = new CustomerRegisteredEvent(
                new CustomerId(),
                Instant.now(),
                new FullName("John", "Doe"),
                new Email("john.doe@example.com")
        );
        publisher.publishEvent(event);
        verify(customerEventListener).handleCustomerRegisteredEvent(event);
        verify(customerNotificationApplicationService).notifyNewRegistration(any(NotifyNewRegistrationInput.class));
    }

    @Test
    void shouldHandleOrderReadyEvent() {
        final OrderReadyEvent event = new OrderReadyEvent(new OrderId(), new CustomerId(), Instant.now());
        publisher.publishEvent(event);
        verify(customerEventListener).handleOrderReadyEvent(event);
        verify(customerLoyaltyPointsApplicationService).addLoyaltyPoints(any(UUID.class), any(String.class));
    }

}