package com.github.allisson95.algashop.ordering.infrastructure.product.client.fake;

import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductCatalogService;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductId;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductName;

import java.util.Optional;

class FakeProductCatalogService implements ProductCatalogService {

    @Override
    public Optional<Product> ofId(final ProductId productId) {
        final Product sampleProduct = Product.builder()
                .id(productId)
                .name(new ProductName("Notebook"))
                .price(new Money("3000"))
                .inStock(true)
                .build();
        return Optional.of(sampleProduct);
    }

}
