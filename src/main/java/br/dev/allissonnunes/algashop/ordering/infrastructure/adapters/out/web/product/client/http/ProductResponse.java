package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal salePrice,
        Boolean inStock
) {

}
