package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
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
            shoppingCartPersistenceEntity.setId(shoppingCart.getId().value());
        }
        shoppingCartPersistenceEntity.setCustomer(this.customerRepository.getReferenceById(shoppingCart.getCustomerId().value()));
        shoppingCartPersistenceEntity.setTotalAmount(shoppingCart.getTotalAmount().value());
        shoppingCartPersistenceEntity.setTotalItems(shoppingCart.getTotalItems().value());
        shoppingCartPersistenceEntity.setCreatedAt(shoppingCart.getCreatedAt());
        shoppingCartPersistenceEntity.replaceItems(mergeItems(shoppingCart, shoppingCartPersistenceEntity));

        shoppingCartPersistenceEntity.setVersion(DomainVersionHandler.getVersion(shoppingCart));

        shoppingCartPersistenceEntity.setDomainEventSupplier(shoppingCart::domainEvents);
        shoppingCartPersistenceEntity.setOnAllEventsPublished(shoppingCart::clearDomainEvents);

        return shoppingCartPersistenceEntity;
    }

    public ShoppingCartItemPersistenceEntity fromDomain(final ShoppingCartItem shoppingCartItem) {
        return merge(new ShoppingCartItemPersistenceEntity(), shoppingCartItem);
    }

    public ShoppingCartItemPersistenceEntity merge(final ShoppingCartItemPersistenceEntity shoppingCartItemPersistenceEntity, final ShoppingCartItem shoppingCartItem) {
        requireNonNull(shoppingCartItemPersistenceEntity, "shoppingCartPersistenceEntity cannot be null");
        requireNonNull(shoppingCartItem, "shoppingCart cannot be null");

        if (isNull(shoppingCartItemPersistenceEntity.getId())) {
            shoppingCartItemPersistenceEntity.setId(shoppingCartItem.getId().value());
        }
        shoppingCartItemPersistenceEntity.setProductId(shoppingCartItem.getProductId().value());
        shoppingCartItemPersistenceEntity.setProductName(shoppingCartItem.getProductName().value());
        shoppingCartItemPersistenceEntity.setPrice(shoppingCartItem.getPrice().value());
        shoppingCartItemPersistenceEntity.setQuantity(shoppingCartItem.getQuantity().value());
        shoppingCartItemPersistenceEntity.setTotalAmount(shoppingCartItem.getTotalAmount().value());
        shoppingCartItemPersistenceEntity.setAvailable(shoppingCartItem.getAvailable());

        shoppingCartItemPersistenceEntity.setVersion(DomainVersionHandler.getVersion(shoppingCartItem));

        return shoppingCartItemPersistenceEntity;
    }

    private Set<ShoppingCartItemPersistenceEntity> mergeItems(final ShoppingCart shoppingCart, final ShoppingCartPersistenceEntity shoppingCartPersistenceEntity) {
        final Set<ShoppingCartItem> shoppingCartItems = shoppingCart.getItems();
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
                .map(shoppingCartItem -> ofNullable(existingItemMap.get(shoppingCartItem.getId().value()))
                        .map(existingItem -> merge(existingItem, shoppingCartItem))
                        .orElse(fromDomain(shoppingCartItem)))
                .collect(toSet());
    }

}
