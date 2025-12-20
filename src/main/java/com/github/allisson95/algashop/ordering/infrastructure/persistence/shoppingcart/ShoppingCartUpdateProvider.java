package com.github.allisson95.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductId;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartProductAdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class ShoppingCartUpdateProvider implements ShoppingCartProductAdjustmentService {

    private final ShoppingCartPersistenceEntityRepository repository;

    @Override
    @Transactional
    public void adjustPrice(final ProductId productId, final Money updatedPrice) {
        this.repository.updateItemPrice(productId.value(), updatedPrice.value());
        this.repository.recalculateTotalAmountForShoppingCartsWithProductId(productId.value());
    }

    @Override
    @Transactional
    public void changeAvailability(final ProductId productId, final boolean available) {
        this.repository.updateItemAvailability(productId.value(), available);
        this.repository.recalculateTotalAmountForShoppingCartsWithProductId(productId.value());
    }

}
