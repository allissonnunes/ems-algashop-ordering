package br.dev.allissonnunes.algashop.ordering.core.domain.model.product;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.DomainException;

public class ProductOutOfStockException extends DomainException {

    public ProductOutOfStockException(final ProductId productId) {
        super("Product %s out of stock".formatted(productId));
    }

}
