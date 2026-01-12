package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.AbstractEventSourceEntity;
import br.dev.allissonnunes.algashop.ordering.domain.model.AggregateRoot;
import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

@Getter
public class ShoppingCart
        extends AbstractEventSourceEntity
        implements AggregateRoot<ShoppingCartId> {

    private ShoppingCartId id;

    private CustomerId customerId;

    private Money totalAmount;

    private Quantity totalItems;

    private Instant createdAt;

    private Set<ShoppingCartItem> items;

    private Long version;

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
        final ShoppingCart shoppingCart = new ShoppingCart(new ShoppingCartId(), customerId, Money.ZERO, Quantity.ZERO, Instant.now(), new LinkedHashSet<>());
        shoppingCart.registerEvent(new ShoppingCartCreatedEvent(shoppingCart.getId(), shoppingCart.getCustomerId(), shoppingCart.getCreatedAt()));
        return shoppingCart;
    }

    public void empty() {
        this.items.clear();
        this.recalculateTotals();
        super.registerEvent(new ShoppingCartEmptiedEvent(this.getId(), this.getCustomerId(), Instant.now()));
    }

    public void addItem(final Product product, final Quantity quantity) {
        requireNonNull(product, "product cannot be null");
        requireNonNull(quantity, "quantity cannot be null");

        product.checkOutOfStock();

        ShoppingCartItem shoppingCartItem;
        try {
            shoppingCartItem = findItem(product.id());
            shoppingCartItem.refresh(product);
            shoppingCartItem.changeQuantity(shoppingCartItem.getQuantity().add(quantity));
        } catch (final ShoppingCartDoesNotContainItemException e) {
            shoppingCartItem = ShoppingCartItem.brandNew(this.getId(), product, quantity);
            this.items.add(shoppingCartItem);
        }

        this.recalculateTotals();

        super.registerEvent(new ShoppingCartItemAddedEvent(this.getId(), this.getCustomerId(), product.id(), Instant.now()));
    }

    public void removeItem(final ShoppingCartItemId shoppingCartItemId) {
        requireNonNull(shoppingCartItemId, "shoppingCartItemId cannot be null");
        final ShoppingCartItem shoppingCartItem = this.findItem(shoppingCartItemId);
        this.items.remove(shoppingCartItem);
        this.recalculateTotals();

        super.registerEvent(new ShoppingCartItemRemovedEvent(this.getId(), this.getCustomerId(), shoppingCartItem.getProductId(), Instant.now()));
    }

    public void refreshItem(final Product product) {
        requireNonNull(product, "product cannot be null");
        this.findItem(product.id()).refresh(product);
        this.recalculateTotals();
    }

    public ShoppingCartItem findItem(final ShoppingCartItemId shoppingCartItemId) {
        requireNonNull(shoppingCartItemId, "shoppingCartItemId cannot be null");
        return this.findShoppingCartItem(item -> item.getId().equals(shoppingCartItemId), () -> new ShoppingCartDoesNotContainItemException(shoppingCartItemId));
    }

    public ShoppingCartItem findItem(final ProductId productId) {
        requireNonNull(productId, "productId cannot be null");
        return this.findShoppingCartItem(item -> item.getProductId().equals(productId), () -> new ShoppingCartDoesNotContainItemException(productId));
    }

    public void changeItemQuantity(final ShoppingCartItemId shoppingCartItemId, final Quantity quantity) {
        requireNonNull(shoppingCartItemId, "shoppingCartItemId cannot be null");
        requireNonNull(quantity, "quantity cannot be null");
        this.findItem(shoppingCartItemId).changeQuantity(quantity);
        this.recalculateTotals();
    }

    public boolean containsUnavailableItems() {
        return this.getItems().stream().anyMatch(not(ShoppingCartItem::getAvailable));
    }

    public boolean isEmpty() {
        return this.getItems().isEmpty();
    }

    private void recalculateTotals() {
        final Money totalAmount = this.getItems().stream().map(ShoppingCartItem::getTotalAmount).reduce(Money.ZERO, Money::add);
        final Quantity totalItemsCount = this.getItems().stream().map(ShoppingCartItem::getQuantity).reduce(Quantity.ZERO, Quantity::add);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItemsCount);
    }

    private ShoppingCartItem findShoppingCartItem(final Predicate<ShoppingCartItem> predicate, final Supplier<DomainException> exceptionSupplier) {
        requireNonNull(predicate, "predicate cannot be null");
        return this.getItems().stream().filter(predicate).findFirst().orElseThrow(exceptionSupplier);
    }

    private void setId(final ShoppingCartId id) {
        requireNonNull(id, "id cannot be null");
        this.id = id;
    }

    private void setCustomerId(final CustomerId customerId) {
        requireNonNull(customerId, "customerId cannot be null");
        this.customerId = customerId;
    }

    private void setTotalAmount(final Money totalAmount) {
        requireNonNull(totalAmount, "totalAmount cannot be null");
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(final Quantity totalItems) {
        requireNonNull(totalItems, "totalItems cannot be null");
        this.totalItems = totalItems;
    }

    private void setCreatedAt(final Instant createdAt) {
        requireNonNull(createdAt, "createdAt cannot be null");
        this.createdAt = createdAt;
    }

    public Set<ShoppingCartItem> getItems() {
        return Collections.unmodifiableSet(items);
    }

    private void setItems(final Set<ShoppingCartItem> items) {
        requireNonNull(items, "items cannot be null");
        this.items = items;
    }

    private Long getVersion() {
        return version;
    }

    private void setVersion(final Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final ShoppingCart that = (ShoppingCart) o;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

}
