package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.valueobject.ProductName;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ProductId;
import net.datafaker.Faker;

public class ProductTestDataBuilder {

    private static final Faker faker = new Faker();

    private ProductTestDataBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
                .id(new ProductId())
                .name(new ProductName(faker.commerce().productName()))
                .price(new Money(faker.commerce().price()))
                .inStock(true);
    }

}
