package com.github.allisson95.algashop.ordering.application.customer.management;

import com.github.allisson95.algashop.ordering.application.utility.Mapper;
import com.github.allisson95.algashop.ordering.domain.model.commons.*;
import com.github.allisson95.algashop.ordering.domain.model.customer.*;
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

    private final Mapper mapper;

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
                mapper.convert(input.address(), Address.class)
        );

        customers.add(registeredCustomer);

        return registeredCustomer.getId().value();
    }

    @Transactional(readOnly = true)
    public CustomerOutput findById(final UUID customerId) {
        requireNonNull(customerId, "customerId cannot be null");
        final CustomerId customerIdentifier = new CustomerId(customerId);
        return customers.ofId(customerIdentifier)
                .map(customer -> mapper.convert(customer, CustomerOutput.class))
                .orElseThrow(() -> new CustomerNotFoundException(customerIdentifier));
    }

    @Transactional
    public void update(final UUID rawCustomerId, final CustomerUpdateInput input) {
        requireNonNull(rawCustomerId, "rawCustomerId cannot be null");
        requireNonNull(input, "input cannot be null");

        final CustomerId customerId = new CustomerId(rawCustomerId);
        final Customer customer = customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.changeName(new FullName(input.firstName(), input.lastName()));
        customer.changePhone(new Phone(input.phone()));

        if (Boolean.TRUE.equals(input.promotionNotificationsAllowed())) {
            customer.enablePromotionNotifications();
        } else {
            customer.disablePromotionNotifications();
        }

        final Address newAddress = mapper.convert(input.address(), Address.class);
        customer.changeAddress(newAddress);

        customers.add(customer);
    }

}
