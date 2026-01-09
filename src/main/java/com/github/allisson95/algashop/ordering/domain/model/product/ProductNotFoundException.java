package com.github.allisson95.algashop.ordering.domain.model.product;

import com.github.allisson95.algashop.ordering.domain.model.DomainEntityNotFoundException;

public class ProductNotFoundException extends DomainEntityNotFoundException {

    public ProductNotFoundException(final ProductId productId) {
        super("Product %s not found".formatted(productId));
    }

}
