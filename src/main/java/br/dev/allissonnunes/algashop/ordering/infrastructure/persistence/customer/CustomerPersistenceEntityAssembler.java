package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Address;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.BirthDate;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
            customerPersistenceEntity.setId(customer.getId().value());
        }

        customerPersistenceEntity.setFirstName(customer.getFullName().firstName());
        customerPersistenceEntity.setLastName(customer.getFullName().lastName());
        customerPersistenceEntity.setBirthDate(Optional.ofNullable(customer.getBirthDate()).map(BirthDate::value).orElse(null));
        customerPersistenceEntity.setEmail(customer.getEmail().value());
        customerPersistenceEntity.setPhone(customer.getPhone().value());
        customerPersistenceEntity.setDocument(customer.getDocument().value());
        customerPersistenceEntity.setPromotionNotificationsAllowed(customer.getPromotionNotificationsAllowed());
        customerPersistenceEntity.setArchived(customer.getArchived());
        customerPersistenceEntity.setRegisteredAt(customer.getRegisteredAt());
        customerPersistenceEntity.setArchivedAt(customer.getArchivedAt());
        customerPersistenceEntity.setLoyaltyPoints(customer.getLoyaltyPoints().value());
        customerPersistenceEntity.setAddress(assembleAddress(customer.getAddress()));
        customerPersistenceEntity.setVersion(DomainVersionHandler.getVersion(customer));
        customerPersistenceEntity.setDomainEventSupplier(customer::domainEvents);
        customerPersistenceEntity.setOnAllEventsPublished(customer::clearDomainEvents);
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
