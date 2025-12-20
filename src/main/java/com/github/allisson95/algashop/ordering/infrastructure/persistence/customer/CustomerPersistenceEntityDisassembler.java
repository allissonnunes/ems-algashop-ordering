package com.github.allisson95.algashop.ordering.infrastructure.persistence.customer;

import com.github.allisson95.algashop.ordering.domain.model.commons.*;
import com.github.allisson95.algashop.ordering.domain.model.customer.BirthDate;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerPersistenceEntityDisassembler {

    public Customer toDomainEntity(final CustomerPersistenceEntity customerPersistenceEntity) {
        final Customer customer = Customer.existingCustomer()
                .id(new CustomerId(customerPersistenceEntity.getId()))
                .fullName(new FullName(customerPersistenceEntity.getFirstName(), customerPersistenceEntity.getLastName()))
                .birthDate(Optional.ofNullable(customerPersistenceEntity.getBirthDate()).map(BirthDate::new).orElse(null))
                .email(new Email(customerPersistenceEntity.getEmail()))
                .phone(new Phone(customerPersistenceEntity.getPhone()))
                .document(new Document(customerPersistenceEntity.getDocument()))
                .promotionNotificationsAllowed(customerPersistenceEntity.getPromotionNotificationsAllowed())
                .archived(customerPersistenceEntity.getArchived())
                .registeredAt(customerPersistenceEntity.getRegisteredAt())
                .archivedAt(customerPersistenceEntity.getArchivedAt())
                .loyaltyPoints(new LoyaltyPoints(customerPersistenceEntity.getLoyaltyPoints()))
                .address(assembleAddress(customerPersistenceEntity.getAddress()))
                .build();
        DomainVersionHandler.setVersion(customer, customerPersistenceEntity.getVersion());
        return customer;
    }

    private Address assembleAddress(final AddressEmbeddable address) {
        return Address.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(new ZipCode(address.getZipCode()))
                .build();
    }

}
