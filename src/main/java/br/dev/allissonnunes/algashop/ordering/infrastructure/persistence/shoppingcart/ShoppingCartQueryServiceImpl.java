package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {

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
