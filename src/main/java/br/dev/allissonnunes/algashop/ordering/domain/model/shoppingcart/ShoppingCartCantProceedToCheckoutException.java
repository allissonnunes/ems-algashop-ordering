package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;

public class ShoppingCartCantProceedToCheckoutException extends DomainException {

    public ShoppingCartCantProceedToCheckoutException() {
        super("Shopping cart can't proceed to checkout.");
    }

}
