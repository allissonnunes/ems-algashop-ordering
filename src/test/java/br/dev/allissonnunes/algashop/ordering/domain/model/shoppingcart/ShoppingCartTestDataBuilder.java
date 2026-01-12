package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;

import java.time.Instant;
import java.util.Set;

public class ShoppingCartTestDataBuilder {

    private CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

    private boolean withItems = true;

    private ShoppingCartTestDataBuilder() {
    }

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCartTestDataBuilder customerId(final CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public ShoppingCartTestDataBuilder withItems(final boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public ShoppingCart build() {
        final ShoppingCart shoppingCart = ShoppingCart.startShopping(this.customerId);

        if (this.withItems) {
            shoppingCart.addItem(ProductTestDataBuilder.anAltProduct().build(), new Quantity(1));
            shoppingCart.addItem(ProductTestDataBuilder.anAltProduct().build(), new Quantity(2));
            shoppingCart.addItem(ProductTestDataBuilder.anAltProduct().build(), new Quantity(1));
        }

        return shoppingCart;
    }

    public static ShoppingCart.ExistingShoppingCartBuilder existingShoppingCart() {
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity itemQuantity = new Quantity(1);
        return ShoppingCart.existingShoppingCart()
                .id(new ShoppingCartId())
                .customerId(new CustomerId())
                .totalAmount(product.price())
                .totalItems(itemQuantity)
                .createdAt(Instant.now())
                .items(Set.of(ShoppingCartItem.brandNew(new ShoppingCartId(), product, itemQuantity)));
    }

}
