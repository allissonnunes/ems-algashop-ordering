package com.github.allisson95.algashop.ordering.infrastructure.persistence.repository;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.configuration.SpringDataJpaConfiguration;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@Import(SpringDataJpaConfiguration.class)
@DataJpaTest(showSql = false)
@ExtendWith(DataJpaCleanUpExtension.class)
class OrderPersistenceEntityRepositoryIT {

    @Autowired
    private OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    @Autowired
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    private CustomerPersistenceEntity customerPersistenceEntity;

    @BeforeEach
    void setUp() {
        final CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        if (!customerPersistenceEntityRepository.existsById(customerId.value())) {
            customerPersistenceEntity = customerPersistenceEntityRepository.persistAndFlush(CustomerPersistenceEntityTestDataBuilder.aCustomer().build());
        }

    }

    @Test
    void shouldPersistOrder() {
        final OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .customer(customerPersistenceEntity)
                .build();

        this.orderPersistenceEntityRepository.persist(entity);

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

        this.orderPersistenceEntityRepository.persist(entity);

        assertWith(entity,
                e -> assertThat(e.getCreatedBy()).isNotNull(),
                e -> assertThat(e.getCreatedAt()).isNotNull(),
                e -> assertThat(e.getLastModifiedBy()).isNotNull(),
                e -> assertThat(e.getLastModifiedAt()).isNotNull()
        );
    }

}