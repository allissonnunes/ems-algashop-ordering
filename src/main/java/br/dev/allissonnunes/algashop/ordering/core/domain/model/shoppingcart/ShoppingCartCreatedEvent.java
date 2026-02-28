package br.dev.allissonnunes.algashop.ordering.core.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerId;

import java.time.Instant;

public record ShoppingCartCreatedEvent(ShoppingCartId shoppingCartId, CustomerId customerId, Instant createdOn) {

}
