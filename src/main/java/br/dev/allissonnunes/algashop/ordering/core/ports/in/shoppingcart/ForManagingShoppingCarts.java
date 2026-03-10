package br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart;

import java.util.UUID;

public interface ForManagingShoppingCarts {

    UUID createNew(UUID rawCustomerId);

    void addItem(ShoppingCartItemInput shoppingCartItemInput);

    void removeItem(UUID rawShoppingCartId, UUID rawShoppingCartItemId);

    void empty(UUID rawShoppingCartId);

    void delete(UUID rawShoppingCartId);

}
