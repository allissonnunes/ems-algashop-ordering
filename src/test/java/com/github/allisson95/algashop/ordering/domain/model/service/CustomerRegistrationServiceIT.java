package com.github.allisson95.algashop.ordering.domain.model.service;

import com.github.allisson95.algashop.ordering.domain.model.entity.Customer;
import com.github.allisson95.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.exception.CustomerEmailIsInUseException;
import com.github.allisson95.algashop.ordering.domain.model.repository.Customers;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CustomerRegistrationServiceIT {

    private static final Faker faker = new Faker();

    @Autowired
    private CustomerRegistrationService service;

    @Autowired
    private Customers customers;

    @Test
    void shouldRegister() {
        final FullName expectedName = new FullName(faker.name().firstName(), faker.name().lastName());
        final BirthDate expectedBirthDate = new BirthDate(faker.timeAndDate().birthday());
        final Email expectedEmail = new Email(faker.internet().emailAddress());
        final Phone expectedPhone = new Phone(faker.phoneNumber().cellPhone());
        final Document expectedDocument = new Document(faker.passport().valid());
        final boolean expectedPromotionNotificationsAllowed = true;
        final Address expectedAddress = Address.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().buildingNumber())
                .complement(faker.address().secondaryAddress())
                .neighborhood(faker.address().secondaryAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(new ZipCode(faker.address().zipCode()))
                .build();

        final Customer registeredCustomer = service.register(
                expectedName,
                expectedBirthDate,
                expectedEmail,
                expectedPhone,
                expectedDocument,
                expectedPromotionNotificationsAllowed,
                expectedAddress
        );

        assertWith(registeredCustomer,
                c -> assertThat(c.id()).isNotNull(),
                c -> assertThat(c.fullName()).isEqualTo(expectedName),
                c -> assertThat(c.birthDate()).isEqualTo(expectedBirthDate),
                c -> assertThat(c.email()).isEqualTo(expectedEmail),
                c -> assertThat(c.phone()).isEqualTo(expectedPhone),
                c -> assertThat(c.document()).isEqualTo(expectedDocument),
                c -> assertThat(c.isPromotionNotificationsAllowed()).isEqualTo(expectedPromotionNotificationsAllowed),
                c -> assertThat(c.address()).isEqualTo(expectedAddress)
        );
    }

    @Test
    void shouldThrowsExceptionIfEmailIsInUse() {
        final FullName expectedName = new FullName(faker.name().firstName(), faker.name().lastName());
        final BirthDate expectedBirthDate = new BirthDate(faker.timeAndDate().birthday());
        final Email expectedEmail = new Email(faker.internet().emailAddress());
        final Phone expectedPhone = new Phone(faker.phoneNumber().cellPhone());
        final Document expectedDocument = new Document(faker.passport().valid());
        final boolean expectedPromotionNotificationsAllowed = true;
        final Address expectedAddress = Address.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().buildingNumber())
                .complement(faker.address().secondaryAddress())
                .neighborhood(faker.address().secondaryAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(new ZipCode(faker.address().zipCode()))
                .build();
        customers.add(CustomerTestDataBuilder.newCustomer().email(expectedEmail).build());

        assertThatExceptionOfType(CustomerEmailIsInUseException.class).isThrownBy(() -> service.register(
                expectedName,
                expectedBirthDate,
                expectedEmail,
                expectedPhone,
                expectedDocument,
                expectedPromotionNotificationsAllowed,
                expectedAddress
        ));
    }

}