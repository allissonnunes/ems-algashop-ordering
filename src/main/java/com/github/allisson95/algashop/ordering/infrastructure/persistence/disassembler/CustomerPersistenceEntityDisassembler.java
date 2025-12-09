package com.github.allisson95.algashop.ordering.infrastructure.persistence.disassembler;

import com.github.allisson95.algashop.ordering.domain.model.entity.Customer;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.util.DomainVersionHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityDisassembler {

    public Customer toDomainEntity(final CustomerPersistenceEntity customerPersistenceEntity) {
        final Customer customer = Customer.existingCustomer()
                .id(new CustomerId(customerPersistenceEntity.getId()))
                .fullName(new FullName(customerPersistenceEntity.getFirstName(), customerPersistenceEntity.getLastName()))
                .birthDate(new BirthDate(customerPersistenceEntity.getBirthDate()))
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
