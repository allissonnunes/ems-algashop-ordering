package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.valueobject.ProductName;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.CustomerId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ProductId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderTest {

    @Test
    void shouldGenerateNewOrder() {
        final Order draftOrder = Order.draft(new CustomerId());
        assertThat(draftOrder).isNotNull();
    }

    @Test
    void shouldAddItem() {
        final Order order = Order.draft(new CustomerId());
        final ProductId productId = new ProductId();
        order.addItem(productId, new ProductName("Smartphone"), new Money(new BigDecimal("1499.99")), new Quantity(1));

        assertThat(order.items()).hasSize(1);
        assertWith(order.items().iterator().next(),
                i -> assertThat(i.id()).isNotNull(),
                i -> assertThat(i.orderId()).isEqualTo(order.id()),
                i -> assertThat(i.productId()).isEqualTo(productId),
                i -> assertThat(i.productName()).isEqualTo(new ProductName("Smartphone")),
                i -> assertThat(i.price()).isEqualTo(new Money(new BigDecimal("1499.99"))),
                i -> assertThat(i.quantity()).isEqualTo(new Quantity(1)),
                i -> assertThat(i.totalAmount()).isEqualTo(new Money(new BigDecimal("1499.99")))
        );
    }

    @Test
    void shouldReturnUnmodifiableItemsToPreventDirectChanges() {
        final Order order = Order.draft(new CustomerId());
        final ProductId productId = new ProductId();
        order.addItem(productId, new ProductName("Smartphone"), new Money(new BigDecimal("1499.99")), new Quantity(1));

        assertThat(order.items()).isUnmodifiable();
    }

    @Test
    void shouldCalculateTotals() {
        final Order order = Order.draft(new CustomerId());
        final ProductId productId = new ProductId();
        order.addItem(productId, new ProductName("Mouse Pad"), new Money(new BigDecimal("79.90")), new Quantity(1));
        order.addItem(productId, new ProductName("Ram Memory 8GB"), new Money(new BigDecimal("129.99")), new Quantity(2));

        assertThat(order.totalAmount()).isEqualTo(new Money(new BigDecimal("339.88")));
        assertThat(order.totalItems()).isEqualTo(new Quantity(3));
    }

}