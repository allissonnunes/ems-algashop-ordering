package com.github.allisson95.algashop.ordering.infrastructure.persistence.entity;

import com.github.allisson95.algashop.ordering.domain.model.utility.IdGenerator;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity.OrderPersistenceEntityBuilder;

import java.math.BigDecimal;

public class OrderPersistenceEntityTestDataBuilder {

    private OrderPersistenceEntityTestDataBuilder() {
    }

    public static OrderPersistenceEntityBuilder existingOrder() {
        return OrderPersistenceEntity.builder()
                .id(IdGenerator.gererateTSID().toLong())
                .customerId(IdGenerator.generateTimeBasedUUID())
                .totalAmount(new BigDecimal("100.00"))
                .totalItems(1)
                .placedAt(null)
                .paidAt(null)
                .cancelledAt(null)
                .readyAt(null)
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD");
    }

}
