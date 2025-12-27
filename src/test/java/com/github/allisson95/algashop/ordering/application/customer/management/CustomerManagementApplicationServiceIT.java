package com.github.allisson95.algashop.ordering.application.customer.management;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.domain.model.customer.*;
import com.github.allisson95.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(DataJpaCleanUpExtension.class)
class CustomerManagementApplicationServiceIT {

    @Autowired
    private CustomerManagementApplicationService service;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @Test
    void shouldCreate() {
        final CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();

        final UUID customerId = service.create(customerInput);

        assertThat(customerId).isNotNull();

        final CustomerOutput customerOutput = service.findById(customerId);
        assertWith(customerOutput,
                c -> assertThat(c).isNotNull(),
                c -> assertThat(c.id()).isEqualTo(customerId),
                c -> assertThat(c.firstName()).isEqualTo(customerInput.firstName()),
                c -> assertThat(c.lastName()).isEqualTo(customerInput.lastName()),
                c -> assertThat(c.birthDate()).isEqualTo(customerInput.birthDate()),
                c -> assertThat(c.email()).isEqualTo(customerInput.email()),
                c -> assertThat(c.phone()).isEqualTo(customerInput.phone()),
                c -> assertThat(c.document()).isEqualTo(customerInput.document()),
                c -> assertThat(c.promotionNotificationsAllowed()).isEqualTo(customerInput.promotionNotificationsAllowed()),
                c -> assertThat(c.loyaltyPoints()).isEqualTo(0),
                c -> assertThat(c.archived()).isFalse(),
                c -> assertThat(c.archivedAt()).isNull(),
                c -> assertThat(c.registeredAt()).isNotNull(),
                c -> assertThat(c.address()).isEqualTo(customerInput.address())
        );

        verify(customerEventListener, times(1)).handleCustomerRegisteredEvent(any(CustomerRegisteredEvent.class));
    }

    @Test
    void shouldUpdate() {
        final CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        final CustomerUpdateInput updateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();
        final UUID customerId = service.create(customerInput);

        service.update(customerId, updateInput);

        final CustomerOutput customerOutput = service.findById(customerId);
        assertWith(customerOutput,
                c -> assertThat(c).isNotNull(),
                c -> assertThat(c.id()).isEqualTo(customerId),
                c -> assertThat(c.firstName()).isEqualTo(updateInput.firstName()),
                c -> assertThat(c.lastName()).isEqualTo(updateInput.lastName()),
                c -> assertThat(c.birthDate()).isEqualTo(customerInput.birthDate()),
                c -> assertThat(c.email()).isEqualTo(customerInput.email()),
                c -> assertThat(c.phone()).isEqualTo(updateInput.phone()),
                c -> assertThat(c.document()).isEqualTo(customerInput.document()),
                c -> assertThat(c.promotionNotificationsAllowed()).isEqualTo(updateInput.promotionNotificationsAllowed()),
                c -> assertThat(c.loyaltyPoints()).isEqualTo(0),
                c -> assertThat(c.archived()).isFalse(),
                c -> assertThat(c.archivedAt()).isNull(),
                c -> assertThat(c.registeredAt()).isNotNull(),
                c -> assertThat(c.address()).isEqualTo(updateInput.address())
        );
    }

    @Test
    void shouldArchive() {
        final CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        final UUID customerId = service.create(customerInput);

        service.archive(customerId);

        final CustomerOutput customerOutput = service.findById(customerId);
        assertWith(customerOutput,
                c -> assertThat(c).isNotNull(),
                c -> assertThat(c.id()).isEqualTo(customerId),
                c -> assertThat(c.archived()).isTrue(),
                c -> assertThat(c.archivedAt()).isNotNull(),
                c -> assertThat(c.archivedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS))
        );

        verify(customerEventListener, times(1)).handleCustomerArchivedEvent(any(CustomerArchivedEvent.class));
    }

    @Test
    void shouldThrowExceptionIfArchiveCustomerThatDoesNotExist() {
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.archive(new CustomerId().value()));
    }

    @Test
    void shouldThrowExceptionIfArchiveCustomerThatIsAlreadyArchived() {
        final CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        final UUID customerId = service.create(customerInput);
        service.archive(customerId);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> service.archive(customerId));
    }

    @Test
    void shouldChangeEmail() {
        final CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        final UUID customerId = service.create(customerInput);

        service.changeEmail(customerId, "emailchanged@email.com");

        final CustomerOutput customerOutput = service.findById(customerId);
        assertWith(customerOutput,
                c -> assertThat(c).isNotNull(),
                c -> assertThat(c.id()).isEqualTo(customerId),
                c -> assertThat(c.email()).isEqualTo("emailchanged@email.com")
        );
    }

    @Test
    void shouldThrowExceptionIfChangeEmailOfNonexistentCustomer() {
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.changeEmail(new CustomerId().value(), "emailchanged@email.com"));
    }

    @Test
    void shouldThrowExceptionIfChangeEmailOfArchivedCustomer() {
        final CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        final UUID customerId = service.create(customerInput);
        service.archive(customerId);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> service.changeEmail(customerId, "emailchanged@email.com"));
    }

    @Test
    void shouldThrowExceptionIfChangeEmailWithInvalidEmail() {
        final CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        final UUID customerId = service.create(customerInput);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> service.changeEmail(customerId, "invalid-email"));
    }

    @Test
    void shouldThrowExceptionIfChangeEmailWithEmailThatAlreadyExistsWithAnotherCustomer() {
        final CustomerInput customerInput1 = CustomerInputTestDataBuilder.aCustomer().email("email1@email.com").build();
        final CustomerInput customerInput2 = CustomerInputTestDataBuilder.aCustomer().email("email2@email.com").build();
        service.create(customerInput1);
        final UUID customerId2 = service.create(customerInput2);

        assertThatExceptionOfType(CustomerEmailIsInUseException.class)
                .isThrownBy(() -> service.changeEmail(customerId2, "email1@email.com"));
    }

}