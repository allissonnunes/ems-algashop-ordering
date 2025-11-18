package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.valueobject.ProductName;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ProductId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    void shouldGenerateOrderItem() {
        final OrderItem orderItem = OrderItem.newOrderItem()
                .orderId(new OrderId())
                .productId(new ProductId())
                .productName(new ProductName("Smartphone"))
                .price(new Money(new BigDecimal("1499.99")))
                .quantity(new Quantity(1))
                .build();
        assertThat(orderItem).isNotNull();
    }

}