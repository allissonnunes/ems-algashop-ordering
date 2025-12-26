package com.github.allisson95.algashop.ordering.domain.model.customer;

import com.github.allisson95.algashop.ordering.domain.model.commons.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customer.archive();

        assertWith(customer,
                c -> assertThat(c.getArchived()).isTrue(),
                c -> assertThat(c.getArchivedAt()).isNotNull(),
                c -> assertThat(c.getFullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
                c -> assertThat(c.getBirthDate()).isNull(),
                c -> assertThat(c.getEmail()).isNotEqualTo(new Email("johndoe@email.com")),
                c -> assertThat(c.getPhone()).isEqualTo(new Phone("000-000-0000")),
                c -> assertThat(c.getDocument()).isEqualTo(new Document("000-00-0000")),
                c -> assertThat(c.getPromotionNotificationsAllowed()).isFalse(),
                c -> assertThat(c.getAddress()).isEqualTo(Address.builder()
                        .street("Bourbon Street")
                        .number("Anonymized")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("South California")
                        .zipCode(new ZipCode("10001"))
                        .complement(null)
                        .build())
        );
    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldThrowException() {
        final Customer customer = CustomerTestDataBuilder.existingAnonymizedCustomer().build();

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(10)))
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive)
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enablePromotionNotifications)
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disablePromotionNotifications)
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeName(new FullName("New", "Name")))
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeEmail(new Email("johndoe@email.com")))
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changePhone(new Phone("New Phone")))
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeAddress(customer.getAddress().toBuilder().complement("new complement").build()))
                .withMessage("Customer is archived and cannot be updated");
    }

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        assertThat(customer.getLoyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldThrowException() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(0)))
                .withMessage("loyaltyPointsToAdd cannot be negative or zero");
    }

    @Test
    void givenValidData_whenCreateNewCustomer_shouldGenerateCustomerRegisteredEvent() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();

        assertThatCollection(customer.domainEvents()).hasSize(1);
        assertThatCollection(customer.domainEvents()).first().isInstanceOf(CustomerRegisteredEvent.class);
        assertThatCollection(customer.domainEvents()).first().isEqualTo(new CustomerRegisteredEvent(customer.getId(), customer.getRegisteredAt()));
    }

    @Test
    void given_unarchivedCustomer_whenArchive_shouldGenerateCustomerArchivedEvent() {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customer.archive();

        assertThatCollection(customer.domainEvents()).hasSize(1);
        assertThatCollection(customer.domainEvents()).first().isInstanceOf(CustomerArchivedEvent.class);
        assertThatCollection(customer.domainEvents()).first().isEqualTo(new CustomerArchivedEvent(customer.getId(), customer.getArchivedAt()));
    }

}