package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Email;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.FullName;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.SpringDataJpaConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Import(SpringDataJpaConfiguration.class)
@DataJpaTest(
        showSql = false,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Persistence(Provider|EntityAssembler|EntityDisassembler)"),
        }
)
@ExtendWith(DataJpaCleanUpExtension.class)
class CustomersIT {

    @Autowired
    private Customers customers;

    @Test
    void shouldPersistAndFind() {
        final Customer newCustomer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(newCustomer);

        final Optional<Customer> possibleCustomer = customers.ofId(newCustomer.getId());

        assertThat(possibleCustomer).isPresent();
        assertWith(possibleCustomer.get(),
                c -> assertThat(c.getId()).isEqualTo(newCustomer.getId()));
    }

    @Test
    void shouldUpdateExistingCustomer() {
        Customer customer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer);

        customer = customers.ofId(customer.getId()).orElseThrow();
        assertThat(customer.getArchived()).isFalse();
        assertThat(customer.getArchivedAt()).isNull();

        customer.archive();

        customers.add(customer);

        customer = customers.ofId(customer.getId()).orElseThrow();
        assertThat(customer.getArchived()).isTrue();
        assertThat(customer.getArchivedAt()).isNotNull();
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        Customer customer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer);

        Customer customer1 = customers.ofId(customer.getId()).orElseThrow();
        Customer customer2 = customers.ofId(customer.getId()).orElseThrow();

        customer1.archive();
        customers.add(customer1);

        customer2.changeName(new FullName("John", "Doe"));

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> customers.add(customer2));

        Customer savedCustomer = customers.ofId(customer.getId()).orElseThrow();

        assertThat(savedCustomer.getArchived()).isTrue();
        assertThat(savedCustomer.getArchivedAt()).isNotNull();
        assertThat(savedCustomer.getFullName()).isNotEqualTo(new FullName("John", "Doe"));
    }

    @Test
    void shouldCountExistingCustomers() {
        assertThat(customers.count()).isZero();

        final Customer customer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer);

        assertThat(customers.count()).isEqualTo(1);
    }

    @Test
    void shouldReturnIfCustomerExists() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer);

        assertThat(customers.exists(customer.getId())).isTrue();
        assertThat(customers.exists(new CustomerId())).isFalse();
    }

    @Test
    void shouldFindByEmail() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer);

        final Optional<Customer> possibleCustomer = customers.ofEmail(customer.getEmail());

        assertThat(possibleCustomer).isPresent();
    }

    @Test
    void shouldFindByEmailReturnsEmptyIfCustomerWithProvidedEmailDoesNotExist() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer);

        final Optional<Customer> possibleCustomer = customers.ofEmail(new Email(UUID.randomUUID() + "@algashop.com"));

        assertThat(possibleCustomer).isEmpty();
    }

    @Test
    void shouldReturnTrueIfEmailIsUnique() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer);

        assertThat(customers.isEmailUnique(customer.getEmail(), customer.getId())).isTrue();
        assertThat(customers.isEmailUnique(new Email(UUID.randomUUID() + "@algashop.com"), new CustomerId())).isTrue();
    }

    @Test
    void shouldReturnFalseIfEmailIsNotUnique() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(customer);

        assertThat(customers.isEmailUnique(customer.getEmail(), new CustomerId())).isFalse();
    }

}
