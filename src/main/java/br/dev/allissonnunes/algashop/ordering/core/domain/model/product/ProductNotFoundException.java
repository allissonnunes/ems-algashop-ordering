package br.dev.allissonnunes.algashop.ordering.core.domain.model.product;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.DomainEntityNotFoundException;

public class ProductNotFoundException extends DomainEntityNotFoundException {

    public ProductNotFoundException(final ProductId productId) {
        super("Product %s not found".formatted(productId));
    }

}
