package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.listener;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.TestcontainersConfiguration;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.Email;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.FullName;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(DataJpaCleanUpExtension.class)
@Import({ TestcontainersConfiguration.class, MapStructTestConfiguration.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CustomerEventListenerIT {

    @Autowired
    private ApplicationEventPublisher publisher;

    @MockitoBean
    private ForConfirmCustomerRegistration forConfirmCustomerRegistration;

    @MockitoBean
    private ForAddingLoyaltyPoints forAddingLoyaltyPoints;

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
        verify(forConfirmCustomerRegistration).confirm(any(UUID.class));
    }

    @Test
    void shouldHandleOrderReadyEvent() {
        final OrderReadyEvent event = new OrderReadyEvent(new OrderId(), new CustomerId(), Instant.now());
        publisher.publishEvent(event);
        verify(customerEventListener).handleOrderReadyEvent(event);
        verify(forAddingLoyaltyPoints).addLoyaltyPoints(any(UUID.class), any(String.class));
    }

}