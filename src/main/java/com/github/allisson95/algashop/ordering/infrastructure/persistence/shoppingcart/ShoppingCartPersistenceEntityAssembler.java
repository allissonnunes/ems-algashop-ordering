package com.github.allisson95.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerRepository;

    public ShoppingCartPersistenceEntity fromDomain(final ShoppingCart shoppingCart) {
        return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartPersistenceEntity merge(final ShoppingCartPersistenceEntity shoppingCartPersistenceEntity, final ShoppingCart shoppingCart) {
        requireNonNull(shoppingCartPersistenceEntity, "shoppingCartPersistenceEntity cannot be null");
        requireNonNull(shoppingCart, "shoppingCart cannot be null");

        if (isNull(shoppingCartPersistenceEntity.getId())) {
            shoppingCartPersistenceEntity.setId(shoppingCart.id().value());
        }
        shoppingCartPersistenceEntity.setCustomer(this.customerRepository.getReferenceById(shoppingCart.customerId().value()));
        shoppingCartPersistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        shoppingCartPersistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        shoppingCartPersistenceEntity.setCreatedAt(shoppingCart.createdAt());
        shoppingCartPersistenceEntity.replaceItems(mergeItems(shoppingCart, shoppingCartPersistenceEntity));

        shoppingCartPersistenceEntity.setVersion(DomainVersionHandler.getVersion(shoppingCart));

        return shoppingCartPersistenceEntity;
    }

    public ShoppingCartItemPersistenceEntity fromDomain(final ShoppingCartItem shoppingCartItem) {
        return merge(new ShoppingCartItemPersistenceEntity(), shoppingCartItem);
    }

    public ShoppingCartItemPersistenceEntity merge(final ShoppingCartItemPersistenceEntity shoppingCartItemPersistenceEntity, final ShoppingCartItem shoppingCartItem) {
        requireNonNull(shoppingCartItemPersistenceEntity, "shoppingCartPersistenceEntity cannot be null");
        requireNonNull(shoppingCartItem, "shoppingCart cannot be null");

        if (isNull(shoppingCartItemPersistenceEntity.getId())) {
            shoppingCartItemPersistenceEntity.setId(shoppingCartItem.id().value());
        }
        shoppingCartItemPersistenceEntity.setProductId(shoppingCartItem.productId().value());
        shoppingCartItemPersistenceEntity.setProductName(shoppingCartItem.productName().value());
        shoppingCartItemPersistenceEntity.setPrice(shoppingCartItem.price().value());
        shoppingCartItemPersistenceEntity.setQuantity(shoppingCartItem.quantity().value());
        shoppingCartItemPersistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
        shoppingCartItemPersistenceEntity.setAvailable(shoppingCartItem.isAvailable());

        shoppingCartItemPersistenceEntity.setVersion(DomainVersionHandler.getVersion(shoppingCartItem));

        return shoppingCartItemPersistenceEntity;
    }

    private Set<ShoppingCartItemPersistenceEntity> mergeItems(final ShoppingCart shoppingCart, final ShoppingCartPersistenceEntity shoppingCartPersistenceEntity) {
        final Set<ShoppingCartItem> shoppingCartItems = shoppingCart.items();
        if (isNull(shoppingCartItems) || shoppingCartItems.isEmpty()) {
            return new LinkedHashSet<>();
        }

        final Set<ShoppingCartItemPersistenceEntity> existingItems = shoppingCartPersistenceEntity.getItems();
        if (isNull(existingItems) || existingItems.isEmpty()) {
            return shoppingCartItems.stream()
                    .map(this::fromDomain)
                    .collect(toSet());
        }

        final Map<UUID, ShoppingCartItemPersistenceEntity> existingItemMap = existingItems.stream()
                .collect(toMap(ShoppingCartItemPersistenceEntity::getId, identity()));

        return shoppingCartItems.stream()
                .map(shoppingCartItem -> ofNullable(existingItemMap.get(shoppingCartItem.id().value()))
                        .map(existingItem -> merge(existingItem, shoppingCartItem))
                        .orElse(fromDomain(shoppingCartItem)))
                .collect(toSet());
    }

}
