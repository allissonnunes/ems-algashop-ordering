package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Email;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
class CustomersPersistenceProvider implements Customers {

    private final CustomerPersistenceEntityRepository repository;

    private final CustomerPersistenceEntityAssembler assembler;

    private final CustomerPersistenceEntityDisassembler disassembler;

    private final EntityManager entityManager;

    @Override
    public Optional<Customer> ofId(final CustomerId customerId) {
        return this.repository.findById(customerId.value())
                .map(this.disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(final CustomerId customerId) {
        return this.repository.existsById(customerId.value());
    }

    @Override
    public void add(final Customer customer) {
        this.repository.findById(customer.getId().value())
                .ifPresentOrElse(
                        customerPersistenceEntity -> this.updateCustomer(customerPersistenceEntity, customer),
                        () -> this.insertCustomer(customer)
                );
    }

    @Override
    public long count() {
        return this.repository.count();
    }

    @Override
    public Optional<Customer> ofEmail(final Email email) {
        return this.repository.findByEmail(email.value())
                .map(this.disassembler::toDomainEntity);
    }

    @Override
    public boolean isEmailUnique(final Email email, final CustomerId exceptedCustomerId) {
        return !this.repository.existsByEmailAndIdNot(email.value(), exceptedCustomerId.value());
    }

    private void updateCustomer(final CustomerPersistenceEntity customerPersistenceEntity, final Customer customer) {
        this.assembler.merge(customerPersistenceEntity, customer);
        this.entityManager.detach(customerPersistenceEntity);
        this.repository.saveAndFlush(customerPersistenceEntity);
        this.updateVersion(customer, customerPersistenceEntity);
    }

    private void insertCustomer(final Customer customer) {
        final CustomerPersistenceEntity customerPersistenceEntity = this.assembler.fromDomain(customer);
        this.repository.saveAndFlush(customerPersistenceEntity);
        this.updateVersion(customer, customerPersistenceEntity);
    }

    @SneakyThrows
    private void updateVersion(final Customer customer, final CustomerPersistenceEntity customerPersistenceEntity) {
        DomainVersionHandler.setVersion(customer, customerPersistenceEntity.getVersion());
    }

}
