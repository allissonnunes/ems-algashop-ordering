package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainEntityNotFoundException;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;

public class ShoppingCartNotFoundException extends DomainEntityNotFoundException {

    public ShoppingCartNotFoundException(final ShoppingCartId shoppingCartId) {
        super("ShoppingCart %s not found".formatted(shoppingCartId));
    }

    public ShoppingCartNotFoundException(final CustomerId customerId) {
        super("ShoppingCart not found for customer %s".formatted(customerId));
    }

}
