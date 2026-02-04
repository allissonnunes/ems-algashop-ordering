package br.dev.allissonnunes.algashop.ordering.infrastructure.product.client.http;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductCatalogService;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductName;
import br.dev.allissonnunes.algashop.ordering.infrastructure.exception.BadGatewayException;
import br.dev.allissonnunes.algashop.ordering.infrastructure.exception.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class ProductCatalogAPIService implements ProductCatalogService {

    private final ProductCatalogClient productCatalogClient;

    @Override
    public Optional<Product> ofId(final ProductId productId) {
        final ProductResponse retrievedProduct;
        try {
            retrievedProduct = productCatalogClient.findById(productId.value());
        } catch (final ResourceAccessException e) {
            throw new GatewayTimeoutException("Product Catalog API Timeout", e);
        } catch (final HttpClientErrorException e) {
            throw new BadGatewayException("Product Catalog API Bad Gateway", e);
        }
        final Product product = Product.builder()
                .id(new ProductId(retrievedProduct.id()))
                .name(new ProductName(retrievedProduct.name()))
                .price(new Money(retrievedProduct.salePrice()))
                .inStock(retrievedProduct.inStock())
                .build();
        return Optional.of(product);
    }

}
