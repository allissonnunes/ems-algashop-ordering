package com.github.allisson95.algashop.ordering.domain.exception;

import com.github.allisson95.algashop.ordering.domain.valueobject.id.ProductId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;

public class ShoppingCartDoesNotContainItemException extends DomainException {

    public ShoppingCartDoesNotContainItemException(final ShoppingCartItemId shoppingCartItemId) {
        super("Shopping cart does not contain item %s".formatted(shoppingCartItemId));
    }

    public ShoppingCartDoesNotContainItemException(final ProductId productId) {
        super("Shopping cart does not contain item with product id %s".formatted(productId));
    }

}
