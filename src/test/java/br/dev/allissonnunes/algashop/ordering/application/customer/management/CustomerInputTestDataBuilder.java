package br.dev.allissonnunes.algashop.ordering.application.customer.management;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
import net.datafaker.Faker;

public class CustomerInputTestDataBuilder {

    private static final Faker faker = new Faker();

    public static CustomerInput.CustomerInputBuilder aCustomer() {
        return CustomerInput.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .birthDate(faker.timeAndDate().birthday())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .document(faker.passport().valid())
                .promotionNotificationsAllowed(faker.bool().bool())
                .address(AddressData.builder()
                        .street(faker.address().streetAddress())
                        .number(faker.address().buildingNumber())
                        .complement(faker.address().secondaryAddress())
                        .neighborhood(faker.address().secondaryAddress())
                        .city(faker.address().city())
                        .state(faker.address().state())
                        .zipCode(faker.address().zipCode())
                        .build()
                );
    }

}
