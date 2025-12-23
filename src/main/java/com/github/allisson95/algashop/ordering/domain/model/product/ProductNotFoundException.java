package com.github.allisson95.algashop.ordering.domain.model.product;

import com.github.allisson95.algashop.ordering.domain.model.DomainException;

public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(final ProductId productId) {
        super("Product %s not found".formatted(productId));
    }

}
