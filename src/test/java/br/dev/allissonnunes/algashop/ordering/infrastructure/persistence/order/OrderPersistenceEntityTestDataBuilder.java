package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.IdGenerator;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity.OrderPersistenceEntityBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public class OrderPersistenceEntityTestDataBuilder {

    private OrderPersistenceEntityTestDataBuilder() {
    }

    public static OrderPersistenceEntityBuilder existingOrder() {
        return OrderPersistenceEntity.builder()
                .id(IdGenerator.gererateTSID().toLong())
                .customer(CustomerPersistenceEntityTestDataBuilder.aCustomer().build())
                .totalAmount(new BigDecimal("264.9"))
                .totalItems(2)
                .placedAt(Instant.now())
                .paidAt(null)
                .canceledAt(null)
                .readyAt(null)
                .billing(null)
                .shipping(null)
                .status("DRAFT")
                .items(Set.of(orderItem1(), orderItem2()))
                .paymentMethod("CREDIT_CARD");
    }

    private static OrderItemPersistenceEntity orderItem1() {
        return OrderItemPersistenceEntity.builder()
                .id(IdGenerator.gererateTSID().toLong())
                .productId(new ProductId().value().toString())
                .productName("Mouse Pad")
                .price(new BigDecimal("75.0"))
                .quantity(1)
                .totalAmount(new BigDecimal("75.0"))
                .build();
    }

    private static OrderItemPersistenceEntity orderItem2() {
        return OrderItemPersistenceEntity.builder()
                .id(IdGenerator.gererateTSID().toLong())
                .productId(new ProductId().value().toString())
                .productName("Mouse Gamer")
                .price(new BigDecimal("189.9"))
                .quantity(1)
                .totalAmount(new BigDecimal("189.9"))
                .build();
    }

}
