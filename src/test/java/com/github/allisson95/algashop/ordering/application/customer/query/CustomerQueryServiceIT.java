package com.github.allisson95.algashop.ordering.application.customer.query;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.domain.model.commons.Email;
import com.github.allisson95.algashop.ordering.domain.model.commons.FullName;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@SpringBootTest
@ExtendWith(DataJpaCleanUpExtension.class)
class CustomerQueryServiceIT {

    @Autowired
    private CustomerQueryService queryService;

    @Autowired
    private Customers customers;

    @Test
    public void shouldFindById() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        CustomerOutput output = queryService.findById(customer.getId().value());

        Assertions.assertThat(output)
                .extracting(
                        CustomerOutput::id,
                        CustomerOutput::firstName,
                        CustomerOutput::email
                ).containsExactly(
                        customer.getId().value(),
                        customer.getFullName().firstName(),
                        customer.getEmail().value()
                );
    }

    @Test
    public void shouldFilterByPage() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Ana", "Silva")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Bruno", "Costa")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Carla", "Souza")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Daniel", "Pereira")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Eduarda", "Santos")).build());

        CustomerFilter filter = new CustomerFilter(0, 2);
        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(3);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByFirstName() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Alice", "Smith")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Bob", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Alice", "Williams")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("alice");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(page.getContent())
                .extracting(CustomerSummaryOutput::firstName)
                .containsOnly("Alice");
    }

    @Test
    public void shouldFilterByEmail() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).email(new Email("user1@test.com")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).email(new Email("test2@algashop.com")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).email(new Email("user3@test.com")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setEmail("test");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(page.getContent())
                .extracting(CustomerSummaryOutput::email)
                .containsExactlyInAnyOrder("user1@test.com", "test2@algashop.com", "user3@test.com");
    }

    @Test
    public void shouldFilterByMultipleParams() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("John", "Doe")).email(new Email("johndoe@email.com")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Jane", "Doe")).email(new Email("janedoe@email.com")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("John", "Smith")).email(new Email("johnsmith@email.com")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("john");
        filter.setEmail("doe");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst().firstName()).isEqualTo("John");
        Assertions.assertThat(page.getContent().getFirst().email()).isEqualTo("johndoe@email.com");
    }

    @Test
    public void shouldOrderByFirstNameAsc() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Zoe", "Doe")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Charlie", "Smith")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Alice", "Williams")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.ASC);

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent().getFirst().firstName()).isEqualTo("Alice");
    }

    @Test
    public void shouldOrderByFirstNameDesc() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Zoe", "Doe")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Charlie", "Smith")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Alice", "Williams")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.DESC);

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent().getFirst().firstName()).isEqualTo("Zoe");
    }

    @Test
    public void givenNonMatchingFilter_shouldReturnEmptyPage() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("John", "Doe")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("NonExistent");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.isEmpty()).isTrue();
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
    }

}