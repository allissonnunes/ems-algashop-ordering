package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.out.shoppingcart.ForObtainingShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ForObtainingShoppingCartsJpaRepository implements ForObtainingShoppingCarts {

    private final ShoppingCartPersistenceEntityRepository repository;

    @Override
    public ShoppingCartOutput findById(final UUID shoppingCartId) {
        requireNonNull(shoppingCartId, "shoppingCartId cannot be null");
        return repository.findById(shoppingCartId)
                .map(toShoppingCartOutput())
                .orElseThrow(() -> new ShoppingCartNotFoundException(new ShoppingCartId(shoppingCartId)));
    }

    @Override
    public ShoppingCartOutput findByCustomerId(final UUID customerId) {
        requireNonNull(customerId, "customerId cannot be null");
        return repository.findByCustomer_Id(customerId)
                .map(toShoppingCartOutput())
                .orElseThrow(() -> new ShoppingCartNotFoundException(new CustomerId(customerId)));
    }

    private Function<ShoppingCartPersistenceEntity, ShoppingCartOutput> toShoppingCartOutput() {
        return entity -> ShoppingCartOutput.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .totalItems(entity.getTotalItems())
                .totalAmount(entity.getTotalAmount())
                .items(entity.getItems().stream()
                        .map(item -> ShoppingCartItemOutput.builder()
                                .id(item.getId())
                                .productId(item.getProductId())
                                .name(item.getProductName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .totalAmount(item.getTotalAmount())
                                .available(item.getAvailable())
                                .build())
                        .toList())
                .build();
    }

}
