package com.github.allisson95.algashop.ordering.domain.model.service;

import com.github.allisson95.algashop.ordering.domain.model.entity.Customer;
import com.github.allisson95.algashop.ordering.domain.model.exception.CustomerEmailIsInUseException;
import com.github.allisson95.algashop.ordering.domain.model.repository.Customers;
import com.github.allisson95.algashop.ordering.domain.model.utility.DomainService;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private final Customers customers;

    public Customer register(
            final FullName fullName,
            final BirthDate birthDate,
            final Email email,
            final Phone phone,
            final Document document,
            final Boolean promotionNotificationsAllowed,
            final Address address) {

        final Customer newCustomer = Customer.newCustomer()
                .fullName(fullName)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .document(document)
                .promotionNotificationsAllowed(promotionNotificationsAllowed)
                .address(address)
                .build();

        verifyEmailUniqueness(newCustomer.email(), newCustomer.id());

        return newCustomer;
    }

    public void changeEmail(final Customer customer, final Email newEmail) {
        verifyEmailUniqueness(newEmail, customer.id());
        customer.changeEmail(newEmail);
    }

    private void verifyEmailUniqueness(final Email email, final CustomerId customerId) {
        if (!customers.isEmailUnique(email, customerId)) {
            throw new CustomerEmailIsInUseException();
        }
    }

}
