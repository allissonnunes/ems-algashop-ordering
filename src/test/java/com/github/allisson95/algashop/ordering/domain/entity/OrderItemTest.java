package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderItemTest {

    Faker faker = new Faker();

    @Test
    void shouldGenerateOrderItem() {
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(faker.number().numberBetween(1, 10));
        final Money expectedTotalAmount = product.price().multiply(quantity);

        final OrderItem orderItem = OrderItem.newOrderItem()
                .orderId(new OrderId())
                .product(product)
                .quantity(quantity)
                .build();

        assertWith(orderItem,
                i -> assertThat(i).isNotNull(),
                i -> assertThat(i.totalAmount()).isEqualTo(expectedTotalAmount)
        );
    }

}