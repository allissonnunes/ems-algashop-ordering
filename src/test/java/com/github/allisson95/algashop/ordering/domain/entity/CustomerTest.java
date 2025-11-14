package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.CustomerArchivedException;
import com.github.allisson95.algashop.ordering.domain.utility.IdGenerator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldThrowException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Customer(
                        IdGenerator.generate(),
                        "John Doe",
                        LocalDate.of(1991, 7, 5),
                        "invalid",
                        "478-256-2504",
                        "255-08-0578",
                        true,
                        Instant.now()
                ))
                .withMessage("invalid is not a well-formed email address");
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomerEmail_shouldThrowException() {
        final Customer customer = new Customer(
                IdGenerator.generate(),
                "John Doe",
                LocalDate.of(1991, 7, 5),
                "johndoe@email.com",
                "478-256-2504",
                "255-08-0578",
                true,
                Instant.now()
        );
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.changeEmail("invalid"))
                .withMessage("invalid is not a well-formed email address");
    }

    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        final Customer customer = new Customer(
                IdGenerator.generate(),
                "John Doe",
                LocalDate.of(1991, 7, 5),
                "johndoe@email.com",
                "478-256-2504",
                "255-08-0578",
                true,
                Instant.now()
        );

        customer.archive();

        assertWith(customer,
                c -> assertThat(c.isArchived()).isTrue(),
                c -> assertThat(c.archivedAt()).isNotNull(),
                c -> assertThat(c.fullName()).isEqualTo("Anonymous"),
                c -> assertThat(c.birthDate()).isNull(),
                c -> assertThat(c.email()).isNotEqualTo("johndoe@email.com"),
                c -> assertThat(c.phone()).isEqualTo("000-000-0000"),
                c -> assertThat(c.document()).isEqualTo("000-00-0000"),
                c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse()
        );
    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldThrowException() {
        final Customer customer = new Customer(
                IdGenerator.generate(),
                "Anonymous",
                null,
                "anonymous@email.com",
                "000-000-0000",
                "000-00-0000",
                true,
                true,
                Instant.now(),
                Instant.now(),
                0
        );

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(10))
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
                .isThrownBy(() -> customer.changeName("New Name"))
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeEmail("New Email"))
                .withMessage("Customer is archived and cannot be updated");

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changePhone("New Phone"))
                .withMessage("Customer is archived and cannot be updated");
    }

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        final Customer customer = new Customer(
                IdGenerator.generate(),
                "John Doe",
                LocalDate.of(1991, 7, 5),
                "johndoe@email.com",
                "478-256-2504",
                "255-08-0578",
                true,
                Instant.now()
        );

        customer.addLoyaltyPoints(10);
        customer.addLoyaltyPoints(20);

        assertThat(customer.loyaltyPoints()).isEqualTo(30);
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldThrowException() {
        final Customer customer = new Customer(
                IdGenerator.generate(),
                "John Doe",
                LocalDate.of(1991, 7, 5),
                "johndoe@email.com",
                "478-256-2504",
                "255-08-0578",
                true,
                Instant.now()
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(-10))
                .withMessage("loyaltyPointsToAdd cannot be negative or zero");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(0))
                .withMessage("loyaltyPointsToAdd cannot be negative or zero");
    }

}