package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderItem;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceEntityAssemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository customerRepository;

    @InjectMocks
    private OrderPersistenceEntityAssembler assembler;

    @BeforeEach
    void setUp() {
        when(customerRepository.getReferenceById(any()))
                .thenAnswer(invocation -> {
                    final UUID customerId = invocation.getArgument(0);
                    return CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build();
                });
    }

    @Test
    void fromDomain() {
        final Order order = OrderTestDataBuilder.anOrder().build();

        final OrderPersistenceEntity entity = assembler.fromDomain(order);

        assertWith(entity,
                e -> assertThat(e.getId()).isEqualTo(order.getId().value().toLong()),
                e -> assertThat(e.getCustomerId()).isEqualTo(order.getCustomerId().value()),
                e -> assertThat(e.getTotalAmount()).isEqualTo(order.getTotalAmount().value()),
                e -> assertThat(e.getTotalItems()).isEqualTo(order.getTotalItems().value()),
                e -> assertThat(e.getPlacedAt()).isEqualTo(order.getPlacedAt()),
                e -> assertThat(e.getPaidAt()).isEqualTo(order.getPaidAt()),
                e -> assertThat(e.getCanceledAt()).isEqualTo(order.getCanceledAt()),
                e -> assertThat(e.getReadyAt()).isEqualTo(order.getReadyAt()),
                e -> assertThat(e.getStatus()).isEqualTo(order.getStatus().name()),
                e -> assertThat(e.getPaymentMethod()).isEqualTo(order.getPaymentMethod().name())
        );
    }

    @Test
    void givenOrderWithoutItems_whenMerge_shouldRemovePersistenceEntityItems() {
        final Order order = OrderTestDataBuilder.anOrder().withItems(false).build();
        final OrderPersistenceEntity existingOrderEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        assertThat(order.getItems()).isEmpty();
        assertThat(existingOrderEntity.getItems()).isNotEmpty();

        assembler.merge(existingOrderEntity, order);

        assertThat(existingOrderEntity.getItems()).isEmpty();
    }

    @Test
    void givenOrderWithItems_whenMerge_shouldAddItemsToPersistenceEntity() {
        final Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
        final OrderPersistenceEntity existingOrderEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().items(new LinkedHashSet<>()).build();

        assertThat(order.getItems()).isNotEmpty();
        assertThat(existingOrderEntity.getItems()).isEmpty();

        assembler.merge(existingOrderEntity, order);

        assertThat(existingOrderEntity.getItems()).isNotEmpty();
        assertThat(existingOrderEntity.getItems()).hasSize(order.getItems().size());
    }

    @Test
    void givenOrderWithItems_whenMerge_shouldUpdateItemsInPersistenceEntity() {
        final Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
        final OrderPersistenceEntity existingOrderEntity = assembler.fromDomain(order);
        final OrderItem itemToRemove = order.getItems().iterator().next();
        order.removeItem(itemToRemove.getId());

        assertThat(order.getItems()).hasSize(1);
        assertThat(existingOrderEntity.getItems()).hasSize(2);

        assembler.merge(existingOrderEntity, order);

        assertWith(existingOrderEntity.getItems(),
                i -> assertThat(i).hasSize(1),
                i -> assertThatCollection(i).doesNotContain(assembler.fromDomain(itemToRemove))
        );
    }

}