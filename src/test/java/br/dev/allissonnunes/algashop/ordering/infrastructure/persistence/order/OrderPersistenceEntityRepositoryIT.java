package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.AbstractInfrastructureIT;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderPersistenceEntityRepositoryIT extends AbstractInfrastructureIT {

    @Autowired
    private OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    @Autowired
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    private CustomerPersistenceEntity customerPersistenceEntity;

    @BeforeEach
    void setUp() {
        final CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        if (!customerPersistenceEntityRepository.existsById(customerId.value())) {
            customerPersistenceEntity = customerPersistenceEntityRepository.saveAndFlush(CustomerPersistenceEntityTestDataBuilder.aCustomer().build());
        }

    }

    @Test
    void shouldPersistOrder() {
        final OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .customer(customerPersistenceEntity)
                .build();

        this.orderPersistenceEntityRepository.save(entity);

        assertWith(this.orderPersistenceEntityRepository.findById(entity.getId()),
                o -> assertThat(o).isPresent(),
                o -> assertThat(o).hasValueSatisfying(
                        e -> assertThat(e.getItems()).hasSize(2)
                )
        );
    }

    @Test
    void shouldCount() {
        assertThat(this.orderPersistenceEntityRepository.count()).isZero();
    }

    @Test
    void shouldSetAuditingFields() {
        final OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .customer(customerPersistenceEntity)
                .build();

        this.orderPersistenceEntityRepository.save(entity);

        assertWith(entity,
                e -> assertThat(e.getCreatedBy()).isNotNull(),
                e -> assertThat(e.getCreatedAt()).isNotNull(),
                e -> assertThat(e.getLastModifiedBy()).isNotNull(),
                e -> assertThat(e.getLastModifiedAt()).isNotNull()
        );
    }

}