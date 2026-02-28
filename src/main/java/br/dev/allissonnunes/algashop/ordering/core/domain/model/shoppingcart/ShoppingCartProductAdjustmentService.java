package br.dev.allissonnunes.algashop.ordering.core.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.product.ProductId;

public interface ShoppingCartProductAdjustmentService {

    void adjustPrice(ProductId productId, Money updatedPrice);

    void changeAvailability(ProductId productId, boolean available);

}
