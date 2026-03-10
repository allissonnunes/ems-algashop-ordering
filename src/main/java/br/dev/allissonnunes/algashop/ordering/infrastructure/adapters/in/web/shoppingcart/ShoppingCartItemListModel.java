package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemOutput;

import java.util.List;

public record ShoppingCartItemListModel(List<ShoppingCartItemOutput> items) {

}
