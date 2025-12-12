package com.github.allisson95.algashop.ordering.infrastructure.persistence.provider;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.DataSourceProxyQueryCountConfiguration;
import com.github.allisson95.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.entity.OrderStatus;
import com.github.allisson95.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.configuration.SpringDataJpaConfiguration;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@Import({ SpringDataJpaConfiguration.class, DataSourceProxyQueryCountConfiguration.class })
@DataJpaTest(
        showSql = false,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Persistence(Provider|EntityAssembler|EntityDisassembler)"),
        }
)
@ExtendWith(DataJpaCleanUpExtension.class)
public class OrdersPersistenceProviderIT {

    @Autowired
    private OrdersPersistenceProvider persistenceProvider;

    @Autowired
    private CustomersPersistenceProvider customersPersistenceProvider;

    @Autowired
    private OrderPersistenceEntityRepository entityRepository;

    @BeforeEach
    public void setup() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(
                    CustomerTestDataBuilder.existingCustomer().build()
            );
        }
    }

    @Test
    public void shouldUpdateAndKeepPersistenceEntityState() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        long orderId = order.id().value().toLong();
        persistenceProvider.add(order);

        var persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PLACED.name());

        assertThat(persistenceEntity.getCreatedBy()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedBy()).isNotNull();

        order = persistenceProvider.ofId(order.id()).orElseThrow();
        order.markAsPaid();
        persistenceProvider.add(order);

        persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PAID.name());

        assertThat(persistenceEntity.getCreatedBy()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedBy()).isNotNull();

    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldAddFindAndNotFailWhenNoTransaction() {
        Order order = OrderTestDataBuilder.anOrder().build();
        persistenceProvider.add(order);

        SQLStatementCountValidator.reset();
        assertThatNoException().isThrownBy(
                () -> persistenceProvider.ofId(order.id())
        );
        SQLStatementCountValidator.assertSelectCount(1); // Validate N+1 problem
    }

}
