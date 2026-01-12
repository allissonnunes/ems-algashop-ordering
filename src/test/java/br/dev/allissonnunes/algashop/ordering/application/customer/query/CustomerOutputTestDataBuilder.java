package br.dev.allissonnunes.algashop.ordering.application.customer.query;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class CustomerOutputTestDataBuilder {

    public static CustomerOutput.CustomerOutputBuilder existing() {
        return CustomerOutput.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.parse("1980-01-01"))
                .email("john.doe@example.com")
                .phone("+1234567890")
                .document("12345")
                .promotionNotificationsAllowed(true)
                .loyaltyPoints(0)
                .registeredAt(Instant.now())
                .archived(false)
                .archivedAt(null)
                .address(AddressData.builder()
                        .street("123 Main Street")
                        .number("123")
                        .complement("Apt 4B")
                        .neighborhood("Central Park")
                        .city("New York")
                        .state("NY")
                        .zipCode("10001")
                        .build());
    }

}
