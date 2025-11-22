package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.ShoppingCartItemIncompatibleProductException;
import com.github.allisson95.algashop.ordering.domain.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ShoppingCartItemTest {

    private final Faker faker = new Faker();

    @Test
    void shouldGenerateShoppingCartItem() {
        final ShoppingCartId shoppingCartId = new ShoppingCartId();
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(faker.number().numberBetween(1, 10));
        final Money expectedTotalAmount = product.price().multiply(quantity);
        final ShoppingCartItem shoppingCartItem = ShoppingCartItem.brandNew(shoppingCartId, product, quantity);

        assertWith(shoppingCartItem,
                i -> assertThat(i.id()).isNotNull(),
                i -> assertThat(i.shoppingCartId()).isEqualTo(shoppingCartId),
                i -> assertThat(i.productId()).isEqualTo(product.id()),
                i -> assertThat(i.productName()).isEqualTo(product.name()),
                i -> assertThat(i.price()).isEqualTo(product.price()),
                i -> assertThat(i.quantity()).isEqualTo(quantity),
                i -> assertThat(i.totalAmount()).isEqualTo(expectedTotalAmount),
                i -> assertThat(i.isAvailable()).isEqualTo(product.inStock())
        );
    }

    @Test
    void givenAShoppingCartItem_whenTryToChangeQuantityToZero_shouldThrowException() {
        final ShoppingCartId shoppingCartId = new ShoppingCartId();
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(faker.number().numberBetween(1, 10));
        final ShoppingCartItem shoppingCartItem = ShoppingCartItem.brandNew(shoppingCartId, product, quantity);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> shoppingCartItem.changeQuantity(Quantity.ZERO))
                .withMessage("newQuantity cannot be zero");
    }

    @Test
    void givenAShoppingCartItem_whenTryToRefreshIncompatibleProduct_shouldThrowException() {
        final ShoppingCartId shoppingCartId = new ShoppingCartId();
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(faker.number().numberBetween(1, 10));
        final ShoppingCartItem shoppingCartItem = ShoppingCartItem.brandNew(shoppingCartId, product, quantity);
        final Product incompatibleProduct = ProductTestDataBuilder.aProduct().build();

        assertThatExceptionOfType(ShoppingCartItemIncompatibleProductException.class)
                .isThrownBy(() -> shoppingCartItem.refresh(incompatibleProduct))
                .withMessage("Shopping cart item with product id %s is incompatible with product id %s".formatted(product.id(), incompatibleProduct.id()));
    }

    @Test
    void givenTwoShoppingCartItemWithSameId_whenCompareThem_shouldBeEquals() {
        final ShoppingCartId shoppingCartId = new ShoppingCartId();
        final ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(faker.number().numberBetween(1, 10));

        final ShoppingCartItem shoppingCartItem1 = ShoppingCartItem.existingShoppingCartItem()
                .id(shoppingCartItemId)
                .shoppingCartId(shoppingCartId)
                .productId(product.id())
                .productName(product.name())
                .price(product.price())
                .quantity(quantity)
                .totalAmount(product.price().multiply(quantity))
                .available(product.inStock())
                .build();
        final ShoppingCartItem shoppingCartItem2 = ShoppingCartItem.existingShoppingCartItem()
                .id(shoppingCartItemId)
                .shoppingCartId(shoppingCartId)
                .productId(product.id())
                .productName(product.name())
                .price(product.price())
                .quantity(quantity)
                .totalAmount(product.price().multiply(quantity))
                .available(product.inStock())
                .build();

        assertThatObject(shoppingCartItem1).isEqualTo(shoppingCartItem2);
        assertThatObject(shoppingCartItem1).hasSameHashCodeAs(shoppingCartItem2);
        assertThatObject(shoppingCartItem1).hasToString(shoppingCartItem2.toString());
    }

}