package com.github.allisson95.algashop.ordering.infrastructure.persistence.disassembler;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.entity.OrderStatus;
import com.github.allisson95.algashop.ordering.domain.model.entity.PaymentMethod;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(final OrderPersistenceEntity orderPersistenceEntity) {
        return Order.existingOrder()
                .id(new OrderId(orderPersistenceEntity.getId()))
                .customerId(new CustomerId(orderPersistenceEntity.getCustomerId()))
                .totalAmount(new Money(orderPersistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(orderPersistenceEntity.getTotalItems()))
                .placedAt(orderPersistenceEntity.getPlacedAt())
                .paidAt(orderPersistenceEntity.getPaidAt())
                .cancelledAt(orderPersistenceEntity.getCancelledAt())
                .readyAt(orderPersistenceEntity.getReadyAt())
//                .billing(null)
//                .shipping(null)
                .status(OrderStatus.valueOf(orderPersistenceEntity.getStatus()))
                .paymentMethod(PaymentMethod.valueOf(orderPersistenceEntity.getPaymentMethod()))
                .items(new HashSet<>())
                .version(orderPersistenceEntity.getVersion())
                .build();
    }

}
