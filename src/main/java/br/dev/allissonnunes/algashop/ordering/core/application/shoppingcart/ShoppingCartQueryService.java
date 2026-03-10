package br.dev.allissonnunes.algashop.ordering.core.application.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.out.shoppingcart.ForObtainingShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ShoppingCartQueryService implements ForQueryingShoppingCarts {

    private final ForObtainingShoppingCarts shoppingCarts;

    @Override
    public ShoppingCartOutput findById(final UUID shoppingCartId) {
        return shoppingCarts.findById(shoppingCartId);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(final UUID customerId) {
        return shoppingCarts.findByCustomerId(customerId);
    }

}
