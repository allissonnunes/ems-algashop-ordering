package com.github.allisson95.algashop.ordering.infrastructure.fake;

import com.github.allisson95.algashop.ordering.domain.model.service.ProductCatalogService;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.ProductName;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.ProductId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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
