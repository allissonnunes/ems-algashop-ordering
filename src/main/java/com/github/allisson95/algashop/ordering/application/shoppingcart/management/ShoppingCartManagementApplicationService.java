package com.github.allisson95.algashop.ordering.application.shoppingcart.management;

import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductCatalogService;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductId;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class ShoppingCartManagementApplicationService {

    private final ShoppingService shoppingService;

    private final ShoppingCarts shoppingCarts;

    private final ProductCatalogService productCatalogService;

    @Transactional
    public UUID createNew(final UUID rawCustomerId) {
        requireNonNull(rawCustomerId, "customerId cannot be null");

        final var customerId = new CustomerId(rawCustomerId);
        final ShoppingCart shoppingCart = shoppingService.startShopping(customerId);

        shoppingCarts.add(shoppingCart);

        return shoppingCart.getId().value();
    }

    @Transactional
    public void addItem(final ShoppingCartItemInput shoppingCartItemInput) {
        requireNonNull(shoppingCartItemInput, "shoppingCartItemInput cannot be null");

        final var shoppingCartId = new ShoppingCartId(shoppingCartItemInput.shoppingCartId());
        final var productId = new ProductId(shoppingCartItemInput.productId());
        final var quantity = new Quantity(shoppingCartItemInput.quantity());

        final ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        final Product product = productCatalogService.ofId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        shoppingCart.addItem(product, quantity);

        shoppingCarts.add(shoppingCart);
    }

}
