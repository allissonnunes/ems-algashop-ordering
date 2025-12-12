package com.github.allisson95.algashop.ordering.domain.model.repository;

import com.github.allisson95.algashop.ordering.domain.model.entity.Customer;
import com.github.allisson95.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.configuration.SpringDataJpaConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@Import(SpringDataJpaConfiguration.class)
@DataJpaTest(
        showSql = false,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Persistence(Provider|EntityAssembler|EntityDisassembler)"),
        }
)
class CustomersIT {

    @Autowired
    private Customers customers;

    @Test
    void shouldPersistAndFind() {
        final Customer newCustomer = CustomerTestDataBuilder.newCustomer().build();
        customers.add(newCustomer);

        final Optional<Customer> possibleCustomer = customers.ofId(newCustomer.id());

        assertThat(possibleCustomer).isPresent();
        assertWith(possibleCustomer.get(),
                c -> assertThat(c.id()).isEqualTo(newCustomer.id()));
    }

}
