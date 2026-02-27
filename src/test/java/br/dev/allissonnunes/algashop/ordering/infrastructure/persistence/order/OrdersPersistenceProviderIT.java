package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderStatus;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.AbstractInfrastructureIT;
import io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class OrdersPersistenceProviderIT extends AbstractInfrastructureIT {

    @Autowired
    private OrdersPersistenceProvider persistenceProvider;

    @Autowired
    private Customers customers;

    @Autowired
    private OrderPersistenceEntityRepository entityRepository;

    @BeforeEach
    public void setup() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(
                    CustomerTestDataBuilder.existingCustomer().build()
            );
        }
    }

    @Test
    public void shouldUpdateAndKeepPersistenceEntityState() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        long orderId = order.getId().value().toLong();
        persistenceProvider.add(order);

        var persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PLACED.name());

        assertThat(persistenceEntity.getCreatedBy()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedBy()).isNotNull();

        order = persistenceProvider.ofId(order.getId()).orElseThrow();
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
                () -> persistenceProvider.ofId(order.getId())
        );
        SQLStatementCountValidator.assertSelectCount(1); // Validate N+1 problem
    }

}
