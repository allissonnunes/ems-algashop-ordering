package com.github.allisson95.algashop.ordering.infrastructure.persistence.assembler;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class OrderPersistenceEntityAssembler {

    public OrderPersistenceEntity fromDomain(final Order order) {
        return merge(new OrderPersistenceEntity(), order);
    }

    public OrderPersistenceEntity merge(final OrderPersistenceEntity orderPersistenceEntity, final Order order) {
        requireNonNull(orderPersistenceEntity, "orderPersistenceEntity cannot be null");
        requireNonNull(order, "order cannot be null");

//        if (isNull(orderPersistenceEntity.getId())) {
        orderPersistenceEntity.setId(order.id().value().toLong());
//        }

        orderPersistenceEntity.setCustomerId(order.customerId().value());
        orderPersistenceEntity.setTotalAmount(order.totalAmount().value());
        orderPersistenceEntity.setTotalItems(order.totalItems().value());
        orderPersistenceEntity.setPlacedAt(order.placedAt());
        orderPersistenceEntity.setPaidAt(order.paidAt());
        orderPersistenceEntity.setCancelledAt(order.cancelledAt());
        orderPersistenceEntity.setReadyAt(order.readyAt());
//        orderPersistenceEntity.setBilling;
//        orderPersistenceEntity.setShipping;
        orderPersistenceEntity.setStatus(order.status().name());
        orderPersistenceEntity.setPaymentMethod(order.paymentMethod().name());
        orderPersistenceEntity.setVersion(order.version());

        return orderPersistenceEntity;
    }

}
