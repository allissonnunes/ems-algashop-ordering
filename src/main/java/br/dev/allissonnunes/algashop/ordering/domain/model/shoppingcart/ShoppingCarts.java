package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.RemoveCapableRepository;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;

import java.util.Optional;

public interface ShoppingCarts extends RemoveCapableRepository<ShoppingCart, ShoppingCartId> {

    Optional<ShoppingCart> ofCustomer(CustomerId customerId);

    boolean existsByCustomer(CustomerId customerId);

}
