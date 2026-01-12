package br.dev.allissonnunes.algashop.ordering.domain.model.product;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;

public class ProductOutOfStockException extends DomainException {

    public ProductOutOfStockException(final ProductId productId) {
        super("Product %s out of stock".formatted(productId));
    }

}
