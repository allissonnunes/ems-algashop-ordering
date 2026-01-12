package br.dev.allissonnunes.algashop.ordering.application.customer.management;

import br.dev.allissonnunes.algashop.ordering.application.utility.Mapper;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.*;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

    private final CustomerRegistrationService service;

    private final Customers customers;

    private final Mapper mapper;

    @Transactional
    public UUID create(final CustomerInput input) {
        requireNonNull(input, "input cannot be null");

        final Customer registeredCustomer = service.register(
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

    @Transactional
    public void archive(final UUID rawCustomerId) {
        requireNonNull(rawCustomerId, "rawCustomerId cannot be null");

        final CustomerId customerId = new CustomerId(rawCustomerId);
        final Customer customer = customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.archive();

        customers.add(customer);
    }

    @Transactional
    public void changeEmail(final UUID rawCustomerId, final String newEmail) {
        requireNonNull(rawCustomerId, "rawCustomerId cannot be null");
        requireNonNull(newEmail, "newEmail cannot be null");

        final CustomerId customerId = new CustomerId(rawCustomerId);
        final Customer customer = customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        service.changeEmail(customer, new Email(newEmail));

        customers.add(customer);
    }

}
