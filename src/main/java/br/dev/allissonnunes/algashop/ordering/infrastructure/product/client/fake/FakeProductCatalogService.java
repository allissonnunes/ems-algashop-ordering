package br.dev.allissonnunes.algashop.ordering.infrastructure.product.client.fake;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductCatalogService;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductName;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
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
