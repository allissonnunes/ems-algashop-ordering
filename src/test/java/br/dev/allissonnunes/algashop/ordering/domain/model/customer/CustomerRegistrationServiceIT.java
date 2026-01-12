package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.*;
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
                c -> assertThat(c.getId()).isNotNull(),
                c -> assertThat(c.getFullName()).isEqualTo(expectedName),
                c -> assertThat(c.getBirthDate()).isEqualTo(expectedBirthDate),
                c -> assertThat(c.getEmail()).isEqualTo(expectedEmail),
                c -> assertThat(c.getPhone()).isEqualTo(expectedPhone),
                c -> assertThat(c.getDocument()).isEqualTo(expectedDocument),
                c -> assertThat(c.getPromotionNotificationsAllowed()).isEqualTo(expectedPromotionNotificationsAllowed),
                c -> assertThat(c.getAddress()).isEqualTo(expectedAddress)
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