package com.github.allisson95.algashop.ordering.domain.valueobject;

import com.github.allisson95.algashop.ordering.domain.valueobject.id.ProductId;
import lombok.Builder;

import java.util.Objects;

@Builder
public record Product(ProductId id, ProductName name, Money price, Boolean inStock) {

    public Product {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(price, "price cannot be null");
        Objects.requireNonNull(inStock, "inStock cannot be null");
    }

}
