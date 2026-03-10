package br.dev.allissonnunes.algashop.ordering.core.ports.in.order;

import org.jspecify.annotations.NonNull;

public interface ForBuyingWithShoppingCart {

    @NonNull String checkout(CheckoutInput input);

}
