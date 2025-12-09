package com.github.allisson95.algashop.ordering.infrastructure.persistence.assembler;

import com.github.allisson95.algashop.ordering.domain.model.entity.Customer;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Address;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.util.DomainVersionHandler;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Component
public class CustomerPersistenceEntityAssembler {

    public CustomerPersistenceEntity fromDomain(final Customer customer) {
        return merge(new CustomerPersistenceEntity(), customer);
    }

    public CustomerPersistenceEntity merge(final CustomerPersistenceEntity customerPersistenceEntity, final Customer customer) {
        requireNonNull(customerPersistenceEntity, "customerPersistenceEntity cannot be null");
        requireNonNull(customer, "customer cannot be null");

        if (isNull(customerPersistenceEntity.getId())) {
            customerPersistenceEntity.setId(customer.id().value());
        }

        customerPersistenceEntity.setFirstName(customer.fullName().firstName());
        customerPersistenceEntity.setLastName(customer.fullName().lastName());
        customerPersistenceEntity.setBirthDate(customer.birthDate().value());
        customerPersistenceEntity.setEmail(customer.email().value());
        customerPersistenceEntity.setPhone(customer.phone().value());
        customerPersistenceEntity.setDocument(customer.document().value());
        customerPersistenceEntity.setPromotionNotificationsAllowed(customer.isPromotionNotificationsAllowed());
        customerPersistenceEntity.setArchived(customer.isArchived());
        customerPersistenceEntity.setRegisteredAt(customer.registeredAt());
        customerPersistenceEntity.setArchivedAt(customer.archivedAt());
        customerPersistenceEntity.setLoyaltyPoints(customer.loyaltyPoints().value());
        customerPersistenceEntity.setAddress(assembleAddress(customer.address()));
        customerPersistenceEntity.setVersion(DomainVersionHandler.getVersion(customer));

        return customerPersistenceEntity;
    }

    private AddressEmbeddable assembleAddress(final Address address) {
        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.state())
                .zipCode(address.zipCode().value())
                .build();
    }

}
