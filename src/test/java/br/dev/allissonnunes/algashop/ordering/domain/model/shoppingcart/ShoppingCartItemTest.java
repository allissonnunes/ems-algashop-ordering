package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
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
                i -> assertThat(i.getId()).isNotNull(),
                i -> assertThat(i.getShoppingCartId()).isEqualTo(shoppingCartId),
                i -> assertThat(i.getProductId()).isEqualTo(product.id()),
                i -> assertThat(i.getProductName()).isEqualTo(product.name()),
                i -> assertThat(i.getPrice()).isEqualTo(product.price()),
                i -> assertThat(i.getQuantity()).isEqualTo(quantity),
                i -> assertThat(i.getTotalAmount()).isEqualTo(expectedTotalAmount),
                i -> assertThat(i.getAvailable()).isEqualTo(product.inStock())
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
        final Product incompatibleProduct = ProductTestDataBuilder.anAltProduct().build();

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