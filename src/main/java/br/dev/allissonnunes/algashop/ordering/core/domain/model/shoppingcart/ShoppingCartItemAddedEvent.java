package br.dev.allissonnunes.algashop.ordering.core.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.product.ProductId;

import java.time.Instant;

public record ShoppingCartItemAddedEvent(
        ShoppingCartId shoppingCartId,
        CustomerId customerId,
        ProductId productId,
        Instant addedOn
) {

}
