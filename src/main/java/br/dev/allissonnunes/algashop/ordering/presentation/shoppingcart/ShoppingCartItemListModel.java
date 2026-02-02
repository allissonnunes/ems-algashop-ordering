package br.dev.allissonnunes.algashop.ordering.presentation.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;

import java.util.List;

public record ShoppingCartItemListModel(List<ShoppingCartItemOutput> items) {

}
