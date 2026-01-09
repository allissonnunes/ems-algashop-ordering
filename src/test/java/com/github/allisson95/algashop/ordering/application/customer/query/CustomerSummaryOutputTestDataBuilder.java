package com.github.allisson95.algashop.ordering.application.customer.query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class CustomerSummaryOutputTestDataBuilder {

    public static CustomerSummaryOutput.CustomerSummaryOutputBuilder existing() {
        return CustomerSummaryOutput.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@email.com")
                .document("12345")
                .phone("1191234564")
                .birthDate(LocalDate.of(1991, 7, 5))
                .loyaltyPoints(0)
                .registeredAt(Instant.now())
                .archivedAt(null)
                .promotionNotificationsAllowed(false)
                .archived(false);
    }

    public static CustomerSummaryOutput.CustomerSummaryOutputBuilder existingAlt1() {
        return CustomerSummaryOutput.builder()
                .id(UUID.randomUUID())
                .firstName("Scott")
                .lastName("Stacey")
                .email("scott1977@email.com")
                .document("98745")
                .phone("119123456")
                .birthDate(LocalDate.of(1977, 1, 5))
                .loyaltyPoints(10)
                .registeredAt(Instant.now())
                .archivedAt(null)
                .promotionNotificationsAllowed(true)
                .archived(false);
    }

}
