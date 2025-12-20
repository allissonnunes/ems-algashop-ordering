package com.github.allisson95.algashop.ordering.application.service;

import com.github.allisson95.algashop.ordering.application.model.AddressData;
import com.github.allisson95.algashop.ordering.application.model.CustomerInput;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerManagementApplicationServiceIT {

    private static final Faker faker = new Faker();

    @Autowired
    private CustomerManagementApplicationService service;

    @Test
    void create() {
        final CustomerInput customerInput = CustomerInput.builder()
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
                        .build())
                .build();

        final UUID uuid = service.create(customerInput);

        assertThat(uuid).isNotNull();
    }

}