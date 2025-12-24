package com.github.allisson95.algashop.ordering.domain.model.shoppingcart;

import com.github.allisson95.algashop.ordering.domain.model.DomainException;

public class ShoppingCartNotFoundException extends DomainException {

    public ShoppingCartNotFoundException(final ShoppingCartId shoppingCartId) {
        super("ShoppingCart %s not found".formatted(shoppingCartId));
    }

}
