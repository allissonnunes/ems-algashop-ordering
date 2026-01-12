package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;

import java.time.Instant;

public record ShoppingCartCreatedEvent(ShoppingCartId shoppingCartId, CustomerId customerId, Instant createdOn) {

}
