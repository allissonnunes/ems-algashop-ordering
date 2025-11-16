package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.CustomerId;

import java.time.Instant;
import java.time.LocalDate;

public final class CustomerTestDataBuilder {

    private CustomerTestDataBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static Customer.NewCustomerBuilder newCustomer() {
        return Customer.newCustomer()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1991, 7, 5)))
                .email(new Email("johndoe@email.com"))
                .phone(new Phone("478-256-2504"))
                .document(new Document("255-08-0578"))
                .promotionNotificationsAllowed(true)
                .address(Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("South California")
                        .zipCode(new ZipCode("10001"))
                        .complement("Apt. 123")
                        .build()
                );
    }

    public static Customer.ExistingCustomerBuilder existingCustomer() {
        return Customer.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1991, 7, 5)))
                .email(new Email("johndoe@email.com"))
                .phone(new Phone("478-256-2504"))
                .document(new Document("255-08-0578"))
                .promotionNotificationsAllowed(true)
                .archived(false)
                .registeredAt(Instant.now())
                .archivedAt(null)
                .loyaltyPoints(LoyaltyPoints.ZERO)
                .address(Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("South California")
                        .zipCode(new ZipCode("10001"))
                        .complement("Apt. 123")
                        .build()
                );
    }

    public static Customer.ExistingCustomerBuilder existingAnonymizedCustomer() {
        return Customer.existingCustomer()
                .id(new CustomerId())
                .id(new CustomerId())
                .fullName(new FullName("Anonymous", "Anonymous"))
                .birthDate(null)
                .email(new Email("anonymous@email.com"))
                .phone(new Phone("000-000-0000"))
                .document(new Document("000-00-0000"))
                .promotionNotificationsAllowed(false)
                .archived(true)
                .registeredAt(Instant.now())
                .archivedAt(Instant.now())
                .loyaltyPoints(new LoyaltyPoints(50))
                .address(Address.builder()
                        .street("Bourbon Street")
                        .number("Anonymized")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("South California")
                        .zipCode(new ZipCode("10001"))
                        .complement(null)
                        .build()
                );
    }

}
