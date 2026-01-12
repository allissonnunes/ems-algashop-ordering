package br.dev.allissonnunes.algashop.ordering.domain.model.product;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record Product(ProductId id, ProductName name, Money price, Boolean inStock) {

    public Product {
        requireNonNull(id, "id cannot be null");
        requireNonNull(name, "name cannot be null");
        requireNonNull(price, "price cannot be null");
        requireNonNull(inStock, "inStock cannot be null");
    }

    public void checkOutOfStock() {
        final boolean isOutOfStock = !inStock();
        if (isOutOfStock) {
            throw new ProductOutOfStockException(this.id());
        }
    }

}
