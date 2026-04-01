package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.product.ProductCatalogService;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.product.ProductId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.product.ProductName;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@ConditionalOnProperty(name = "algashop.integrations.product-catalog.provider", havingValue = "PRODUCT_CATALOG")
@Service
@RequiredArgsConstructor
class ProductCatalogAPIService implements ProductCatalogService {

    private final ResilientProductCatalogClient productCatalogClient;

    @Override
    public Optional<Product> ofId(final ProductId productId) {
        return productCatalogClient.findById(productId.value())
                .map(retrievedProduct -> Product.builder()
                        .id(new ProductId(retrievedProduct.id()))
                        .name(new ProductName(retrievedProduct.name()))
                        .price(new Money(retrievedProduct.salePrice()))
                        .inStock(retrievedProduct.inStock())
                        .build());
    }

}
