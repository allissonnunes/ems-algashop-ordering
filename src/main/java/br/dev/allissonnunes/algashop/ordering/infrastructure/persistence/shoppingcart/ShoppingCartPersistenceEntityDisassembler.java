package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductName;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

@Component
public class ShoppingCartPersistenceEntityDisassembler {

    public ShoppingCart toDomainEntity(final ShoppingCartPersistenceEntity shoppingCartPersistenceEntity) {
        requireNonNull(shoppingCartPersistenceEntity, "shoppingCartPersistenceEntity cannot be null");

        final ShoppingCart shoppingCart = ShoppingCart.existingShoppingCart()
                .id(new ShoppingCartId(shoppingCartPersistenceEntity.getId()))
                .customerId(new CustomerId(shoppingCartPersistenceEntity.getCustomerId()))
                .totalAmount(new Money(shoppingCartPersistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(shoppingCartPersistenceEntity.getTotalItems()))
                .createdAt(shoppingCartPersistenceEntity.getCreatedAt())
                .items(assembleShoppingCartItems(shoppingCartPersistenceEntity.getItems()))
                .build();

        DomainVersionHandler.setVersion(shoppingCart, shoppingCartPersistenceEntity.getVersion());

        return shoppingCart;
    }

    private Set<ShoppingCartItem> assembleShoppingCartItems(final Set<ShoppingCartItemPersistenceEntity> items) {
        if (isNull(items) || items.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return items.stream()
                .map(sci -> {
                    final ShoppingCartItem shoppingCartItem = ShoppingCartItem.existingShoppingCartItem()
                            .id(new ShoppingCartItemId(sci.getId()))
                            .shoppingCartId(new ShoppingCartId(sci.getShoppingCartId()))
                            .productId(new ProductId(sci.getProductId()))
                            .productName(new ProductName(sci.getProductName()))
                            .price(new Money(sci.getPrice()))
                            .quantity(new Quantity(sci.getQuantity()))
                            .totalAmount(new Money(sci.getTotalAmount()))
                            .available(sci.getAvailable())
                            .build();

                    DomainVersionHandler.setVersion(shoppingCartItem, sci.getVersion());

                    return shoppingCartItem;
                })
                .collect(toSet());
    }

}
