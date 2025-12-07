package com.github.allisson95.algashop.ordering.domain.model.entity;

import com.github.allisson95.algashop.ordering.domain.model.exception.DomainException;
import com.github.allisson95.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainItemException;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

public class ShoppingCart implements AggregateRoot<ShoppingCartId> {

    private ShoppingCartId id;

    private CustomerId customerId;

    private Money totalAmount;

    private Quantity totalItems;

    private Instant createdAt;

    private Set<ShoppingCartItem> items;

    @Builder(builderClassName = "ExistingShoppingCartBuilder", builderMethodName = "existingShoppingCart")
    private ShoppingCart(final ShoppingCartId id, final CustomerId customerId, final Money totalAmount, final Quantity totalItems, final Instant createdAt, final Set<ShoppingCartItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setCreatedAt(createdAt);
        this.setItems(items);
    }

    public static ShoppingCart startShopping(final CustomerId customerId) {
        return new ShoppingCart(new ShoppingCartId(), customerId, Money.ZERO, Quantity.ZERO, Instant.now(), new LinkedHashSet<>());
    }

    public void empty() {
        this.items.clear();
        this.recalculateTotals();
    }

    public void addItem(final Product product, final Quantity quantity) {
        requireNonNull(product, "product cannot be null");
        requireNonNull(quantity, "quantity cannot be null");

        product.checkOutOfStock();

        ShoppingCartItem shoppingCartItem;
        try {
            shoppingCartItem = findItem(product.id());
            shoppingCartItem.refresh(product);
            shoppingCartItem.changeQuantity(shoppingCartItem.quantity().add(quantity));
        } catch (final ShoppingCartDoesNotContainItemException e) {
            shoppingCartItem = ShoppingCartItem.brandNew(this.id(), product, quantity);
            this.items.add(shoppingCartItem);
        }

        this.recalculateTotals();
    }

    public void removeItem(final ShoppingCartItemId shoppingCartItemId) {
        requireNonNull(shoppingCartItemId, "shoppingCartItemId cannot be null");
        final ShoppingCartItem shoppingCartItem = this.findItem(shoppingCartItemId);
        this.items.remove(shoppingCartItem);
        this.recalculateTotals();
    }

    public void refreshItem(final Product product) {
        requireNonNull(product, "product cannot be null");
        this.findItem(product.id()).refresh(product);
        this.recalculateTotals();
    }

    public ShoppingCartItem findItem(final ShoppingCartItemId shoppingCartItemId) {
        requireNonNull(shoppingCartItemId, "shoppingCartItemId cannot be null");
        return this.findShoppingCartItem(item -> item.id().equals(shoppingCartItemId), () -> new ShoppingCartDoesNotContainItemException(shoppingCartItemId));
    }

    public ShoppingCartItem findItem(final ProductId productId) {
        requireNonNull(productId, "productId cannot be null");
        return this.findShoppingCartItem(item -> item.productId().equals(productId), () -> new ShoppingCartDoesNotContainItemException(productId));
    }

    public void changeItemQuantity(final ShoppingCartItemId shoppingCartItemId, final Quantity quantity) {
        requireNonNull(shoppingCartItemId, "shoppingCartItemId cannot be null");
        requireNonNull(quantity, "quantity cannot be null");
        this.findItem(shoppingCartItemId).changeQuantity(quantity);
        this.recalculateTotals();
    }

    public boolean containsUnavailableItems() {
        return this.items().stream().anyMatch(not(ShoppingCartItem::isAvailable));
    }

    public boolean isEmpty() {
        return this.items().isEmpty();
    }

    private void recalculateTotals() {
        final Money totalAmount = this.items().stream().map(ShoppingCartItem::totalAmount).reduce(Money.ZERO, Money::add);
        final Quantity totalItemsCount = this.items().stream().map(ShoppingCartItem::quantity).reduce(Quantity.ZERO, Quantity::add);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItemsCount);
    }

    private ShoppingCartItem findShoppingCartItem(final Predicate<ShoppingCartItem> predicate, final Supplier<DomainException> exceptionSupplier) {
        requireNonNull(predicate, "predicate cannot be null");
        return this.items().stream().filter(predicate).findFirst().orElseThrow(exceptionSupplier);
    }

    public ShoppingCartId id() {
        return id;
    }

    private void setId(final ShoppingCartId id) {
        requireNonNull(id, "id cannot be null");
        this.id = id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    private void setCustomerId(final CustomerId customerId) {
        requireNonNull(customerId, "customerId cannot be null");
        this.customerId = customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    private void setTotalAmount(final Money totalAmount) {
        requireNonNull(totalAmount, "totalAmount cannot be null");
        this.totalAmount = totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    private void setTotalItems(final Quantity totalItems) {
        requireNonNull(totalItems, "totalItems cannot be null");
        this.totalItems = totalItems;
    }

    public Instant createdAt() {
        return createdAt;
    }

    private void setCreatedAt(final Instant createdAt) {
        requireNonNull(createdAt, "createdAt cannot be null");
        this.createdAt = createdAt;
    }

    public Set<ShoppingCartItem> items() {
        return Collections.unmodifiableSet(items);
    }

    private void setItems(final Set<ShoppingCartItem> items) {
        requireNonNull(items, "items cannot be null");
        this.items = items;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final ShoppingCart that = (ShoppingCart) o;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

}
