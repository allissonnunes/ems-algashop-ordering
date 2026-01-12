package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;

public class ShoppingCartDoesNotContainItemException extends DomainException {

    public ShoppingCartDoesNotContainItemException(final ShoppingCartItemId shoppingCartItemId) {
        super("Shopping cart does not contain item %s".formatted(shoppingCartItemId));
    }

    public ShoppingCartDoesNotContainItemException(final ProductId productId) {
        super("Shopping cart does not contain item with product id %s".formatted(productId));
    }

}
