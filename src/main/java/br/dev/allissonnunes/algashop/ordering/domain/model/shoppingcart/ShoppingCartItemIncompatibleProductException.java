package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;

public class ShoppingCartItemIncompatibleProductException extends DomainException {

    public ShoppingCartItemIncompatibleProductException(final ProductId actualProductId, final ProductId incompatibleProductId) {
        super("Shopping cart item with product id %s is incompatible with product id %s".formatted(actualProductId, incompatibleProductId));
    }

}
