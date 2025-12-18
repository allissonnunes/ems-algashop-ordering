package com.github.allisson95.algashop.ordering.domain.model.exception;

public class ShoppingCartCantProceedToCheckoutException extends DomainException {

    public ShoppingCartCantProceedToCheckoutException() {
        super("Shopping cart can't proceed to checkout.");
    }

}
