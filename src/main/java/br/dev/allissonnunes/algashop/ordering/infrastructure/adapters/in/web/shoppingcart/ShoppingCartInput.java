package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShoppingCartInput(@NotNull UUID customerId) {

}
