package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderItemTest {

    Faker faker = new Faker();

    @Test
    void shouldGenerateOrderItem() {
        final OrderId orderId = new OrderId();
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(faker.number().numberBetween(1, 10));
        final Money expectedTotalAmount = product.price().multiply(quantity);

        final OrderItem orderItem = OrderItem.newOrderItem()
                .orderId(orderId)
                .product(product)
                .quantity(quantity)
                .build();

        assertWith(orderItem,
                i -> assertThat(i).isNotNull(),
                i -> assertThat(i.getOrderId()).isEqualTo(orderId),
                i -> assertThat(i.getProductId()).isEqualTo(product.id()),
                i -> assertThat(i.getProductName()).isEqualTo(product.name()),
                i -> assertThat(i.getPrice()).isEqualTo(product.price()),
                i -> assertThat(i.getQuantity()).isEqualTo(quantity),
                i -> assertThat(i.getTotalAmount()).isEqualTo(expectedTotalAmount)
        );
    }

}