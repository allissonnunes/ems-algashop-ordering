package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductOutOfStockException;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

class ShoppingCartTest {

    @Test
    void shouldGenerateEmptyShoppingCart() {
        final CustomerId customerId = new CustomerId();
        final ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);

        assertWith(shoppingCart,
                c -> assertThat(c.getId()).isNotNull(),
                c -> assertThat(c.getCustomerId()).isEqualTo(customerId),
                c -> assertThat(c.getTotalAmount()).isEqualTo(Money.ZERO),
                c -> assertThat(c.getTotalItems()).isEqualTo(Quantity.ZERO),
                c -> assertThatTemporal(c.getCreatedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS)),
                c -> assertThat(c.isEmpty()).isTrue()
        );
    }

    @Test
    void givenUnavailableProduct_whenTryToAddToShoppingCart_shouldThrowException() {
        final CustomerId customerId = new CustomerId();
        final ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
        final Product outOfStockProduct = ProductTestDataBuilder.anOutOfStockProduct().build();
        final Quantity quantity = new Quantity(1);

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> shoppingCart.addItem(outOfStockProduct, quantity))
                .withMessage("Product %s out of stock".formatted(outOfStockProduct.id()));
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void givenACartWithItems_whenTryToAddSameProduct_shouldUpdateItemQuantity() {
        final CustomerId customerId = new CustomerId();
        final ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(1);
        shoppingCart.addItem(product, quantity);

        assertWith(shoppingCart.getItems().iterator().next(),
                i -> assertThat(i.getProductId()).isEqualTo(product.id()),
                i -> assertThat(i.getProductName()).isEqualTo(product.name()),
                i -> assertThat(i.getPrice()).isEqualTo(product.price()),
                i -> assertThat(i.getAvailable()).isEqualTo(product.inStock())
        );

        final Product updatedProduct = ProductTestDataBuilder.aProduct().id(product.id()).build();
        final Quantity quantity2 = new Quantity(3);

        shoppingCart.addItem(updatedProduct, quantity2);

        assertWith(shoppingCart,
                c -> assertThat(c.getItems()).hasSize(1),
                c -> assertThat(c.getTotalItems()).isEqualTo(new Quantity(4)),
                c -> assertThat(c.getTotalAmount()).isEqualTo(updatedProduct.price().multiply(new Quantity(4))),
                c -> assertThatCollection(c.getItems()).first().satisfies(i -> {
                    assertThat(i.getProductId()).isEqualTo(updatedProduct.id());
                    assertThat(i.getProductName()).isEqualTo(updatedProduct.name());
                    assertThat(i.getPrice()).isEqualTo(updatedProduct.price());
                    assertThat(i.getAvailable()).isEqualTo(updatedProduct.inStock());
                })
        );
    }

    @Test
    void shouldAddItem() {
        final CustomerId customerId = new CustomerId();
        final ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
        final Product product1 = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity1 = new Quantity(1);
        final Product product2 = ProductTestDataBuilder.anAltProduct().build();
        final Quantity quantity2 = new Quantity(2);
        final Quantity expectedTotalItems = quantity1.add(quantity2);
        final Money expectedTotalAmount = product1.price().multiply(quantity1).add(product2.price().multiply(quantity2));

        shoppingCart.addItem(product1, quantity1);
        shoppingCart.addItem(product2, quantity2);

        assertWith(shoppingCart,
                c -> assertThat(c.getItems()).hasSize(2),
                c -> assertThat(c.getTotalItems()).isEqualTo(expectedTotalItems),
                c -> assertThat(c.getTotalAmount()).isEqualTo(expectedTotalAmount),
                c -> assertThat(c.isEmpty()).isFalse()
        );
    }

    @Test
    void givenShoppingCart_whenTryToRemoveItemThatIsNotInCart_shouldThrowException() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        final ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();

        assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> shoppingCart.removeItem(shoppingCartItemId))
                .withMessage("Shopping cart does not contain item %s".formatted(shoppingCartItemId));
    }

    @Test
    void shouldRemoveItem() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        final ShoppingCartItem itemToRemove = shoppingCart.getItems().iterator().next();
        final Quantity actualTotalItems = shoppingCart.getTotalItems();
        final Money actualTotalAmount = shoppingCart.getTotalAmount();

        final Quantity expectedTotalItems = new Quantity(actualTotalItems.value() - itemToRemove.getQuantity().value());
        final Money expectedTotalAmount = new Money(actualTotalAmount.value().subtract(itemToRemove.getTotalAmount().value()));

        shoppingCart.removeItem(itemToRemove.getId());

        assertWith(shoppingCart,
                c -> assertThat(c.getItems()).hasSize(2),
                c -> assertThat(c.getTotalItems()).isEqualTo(expectedTotalItems),
                c -> assertThat(c.getTotalAmount()).isEqualTo(expectedTotalAmount)
        );
    }

    @Test
    void shouldClearShoppingCart() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        final Quantity expectedTotalItems = Quantity.ZERO;
        final Money expectedTotalAmount = Money.ZERO;

        shoppingCart.empty();

        assertWith(shoppingCart,
                c -> assertThat(c.getItems()).isEmpty(),
                c -> assertThat(c.getTotalItems()).isEqualTo(expectedTotalItems),
                c -> assertThat(c.getTotalAmount()).isEqualTo(expectedTotalAmount),
                c -> assertThat(c.isEmpty()).isTrue()
        );
    }

    @Test
    void shouldReturnUnmodifiableItemsToPreventDirectChanges() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        assertThatCollection(shoppingCart.getItems()).isUnmodifiable();
    }

    @Test
    void givenTwoShoppingCartWithSameId_whenCompareThem_shouldBeEquals() {
        final ShoppingCartId shoppingCartId = new ShoppingCartId();
        final ShoppingCart shoppingCart1 = ShoppingCartTestDataBuilder.existingShoppingCart().id(shoppingCartId).build();
        final ShoppingCart shoppingCart2 = ShoppingCartTestDataBuilder.existingShoppingCart().id(shoppingCartId).build();

        assertThatObject(shoppingCart1).isEqualTo(shoppingCart2);
        assertThatObject(shoppingCart1).hasSameHashCodeAs(shoppingCart2);
        assertThatObject(shoppingCart1).hasToString(shoppingCart2.toString());
    }

}