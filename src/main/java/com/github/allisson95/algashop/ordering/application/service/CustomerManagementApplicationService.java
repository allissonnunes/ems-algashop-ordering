package com.github.allisson95.algashop.ordering.application.service;

import com.github.allisson95.algashop.ordering.application.model.CustomerInput;
import com.github.allisson95.algashop.ordering.domain.model.commons.*;
import com.github.allisson95.algashop.ordering.domain.model.customer.BirthDate;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class CustomerManagementApplicationService {

    private final CustomerRegistrationService registrationService;

    private final Customers customers;

    @Transactional
    public UUID create(final CustomerInput input) {
        requireNonNull(input, "input cannot be null");

        final Customer registeredCustomer = registrationService.register(
                new FullName(input.firstName(), input.lastName()),
                new BirthDate(input.birthDate()),
                new Email(input.email()),
                new Phone(input.phone()),
                new Document(input.document()),
                input.promotionNotificationsAllowed(),
                Address.builder()
                        .street(input.address().street())
                        .number(input.address().number())
                        .complement(input.address().complement())
                        .neighborhood(input.address().neighborhood())
                        .city(input.address().city())
                        .state(input.address().state())
                        .zipCode(new ZipCode(input.address().zipCode()))
                        .build()
        );

        customers.add(registeredCustomer);

        return registeredCustomer.id().value();
    }

}
