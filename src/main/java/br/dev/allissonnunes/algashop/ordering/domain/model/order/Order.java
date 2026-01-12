package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.AbstractEventSourceEntity;
import br.dev.allissonnunes.algashop.ordering.domain.model.AggregateRoot;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Getter
public class Order
        extends AbstractEventSourceEntity
        implements AggregateRoot<OrderId> {

    private OrderId id;

    private CustomerId customerId;

    private Money totalAmount;

    private Quantity totalItems;

    private Instant placedAt;

    private Instant paidAt;

    private Instant canceledAt;

    private Instant readyAt;

    private Billing billing;

    private Shipping shipping;

    private OrderStatus status;

    private PaymentMethod paymentMethod;

    private Set<OrderItem> items;

    private Long version;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existingOrder")
    private Order(final OrderId id, final CustomerId customerId, final Money totalAmount, final Quantity totalItems, final Instant placedAt, final Instant paidAt, final Instant canceledAt, final Instant readyAt, final Billing billing, final Shipping shipping, final OrderStatus status, final PaymentMethod paymentMethod, final Set<OrderItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCanceledAt(canceledAt);
        this.setReadyAt(readyAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setItems(items);
    }

    public static Order draft(final CustomerId customerId) {
        return new Order(new OrderId(), customerId, Money.ZERO, Quantity.ZERO, null, null, null, null, null, null, OrderStatus.DRAFT, null, new LinkedHashSet<>());
    }

    public void markAsPaid() {
        this.changeStatus(OrderStatus.PAID);
        this.setPaidAt(Instant.now());

        super.registerEvent(new OrderPaidEvent(this.getId(), this.getCustomerId(), this.getPaidAt()));
    }

    public void markAsReady() {
        this.changeStatus(OrderStatus.READY);
        this.setReadyAt(Instant.now());

        super.registerEvent(new OrderReadyEvent(this.getId(), this.getCustomerId(), this.getReadyAt()));
    }

    public void cancel() {
        this.changeStatus(OrderStatus.CANCELED);
        this.setCanceledAt(Instant.now());

        super.registerEvent(new OrderCanceledEvent(this.getId(), this.getCustomerId(), this.getCanceledAt()));
    }

    public void place() {
        this.verifyIfCanChangeToPlaced();
        this.changeStatus(OrderStatus.PLACED);
        this.setPlacedAt(Instant.now());

        super.registerEvent(new OrderPlacedEvent(this.getId(), this.getCustomerId(), this.getPlacedAt()));
    }

    public boolean isPaid() {
        return OrderStatus.PAID.equals(this.getStatus());
    }

    public boolean isCanceled() {
        return OrderStatus.CANCELED.equals(this.getStatus());
    }

    public boolean isReady() {
        return OrderStatus.READY.equals(this.getStatus());
    }

    public boolean isDraft() {
        return OrderStatus.DRAFT.equals(this.getStatus());
    }

    public boolean isPlaced() {
        return OrderStatus.PLACED.equals(this.getStatus());
    }

    public void addItem(final Product product, final Quantity quantity) {
        requireNonNull(product, "product cannot be null");
        requireNonNull(quantity, "quantity cannot be null");

        this.verifyIfChangeable();

        product.checkOutOfStock();

        final OrderItem orderItem = OrderItem.newOrderItem()
                .orderId(this.getId())
                .product(product)
                .quantity(quantity)
                .build();

        if (this.items == null) {
            this.setItems(new LinkedHashSet<>());
        }

        this.items.add(orderItem);
        this.recalculateTotals();
    }

    public void changePaymentMethod(final PaymentMethod newPaymentMethod) {
        requireNonNull(newPaymentMethod, "newPaymentMethod cannot be null");
        this.verifyIfChangeable();
        this.setPaymentMethod(newPaymentMethod);
    }

    public void changeBilling(final Billing newBilling) {
        requireNonNull(newBilling, "newBilling cannot be null");
        this.verifyIfChangeable();
        this.setBilling(newBilling);
    }

    public void changeShipping(final Shipping newShipping) {
        requireNonNull(newShipping, "newShipping cannot be null");

        this.verifyIfChangeable();

        if (newShipping.expectedDeliveryDate().isBefore(LocalDate.now())) {
            throw new OrderInvalidShippingDeliveryDateException(this.getId());
        }

        this.setShipping(newShipping);
        this.recalculateTotals();
    }

    public void changeItemQuantity(final OrderItemId orderItemId, final Quantity newQuantity) {
        requireNonNull(orderItemId, "orderItemId cannot be null");
        requireNonNull(newQuantity, "newQuantity cannot be null");

        this.verifyIfChangeable();

        final OrderItem orderItem = findOrderItem(orderItemId);
        orderItem.changeQuantity(newQuantity);

        this.recalculateTotals();
    }

    public void removeItem(final OrderItemId orderItemId) {
        requireNonNull(orderItemId, "orderItemId cannot be null");
        this.verifyIfChangeable();
        final OrderItem orderItem = findOrderItem(orderItemId);
        this.items.remove(orderItem);
        this.recalculateTotals();
    }

    private void changeStatus(final OrderStatus newStatus) {
        requireNonNull(newStatus, "newStatus cannot be null");
        if (this.getStatus().cantBeUpdatedTo(newStatus)) {
            throw new OrderStatusCannotBeChangedException(this.getId(), this.getStatus(), newStatus);
        }
        this.setStatus(newStatus);
    }

    private void recalculateTotals() {
        final Money shippingCost = Optional.ofNullable(this.getShipping()).map(Shipping::cost).orElse(Money.ZERO);
        final Money totalItemsAmount = this.getItems().stream().map(OrderItem::getTotalAmount).reduce(Money.ZERO, Money::add);
        final Quantity totalItemsCount = this.getItems().stream().map(OrderItem::getQuantity).reduce(Quantity.ZERO, Quantity::add);

        final Money totalOrderAmount = totalItemsAmount.add(shippingCost);

        this.setTotalAmount(totalOrderAmount);
        this.setTotalItems(totalItemsCount);
    }

    private void verifyIfCanChangeToPlaced() {
        if (isNull(this.getBilling())) {
            throw OrderCannotBePlacedException.becauseHasNoBillingInfo(this.getId());
        }
        if (isNull(this.getShipping())) {
            throw OrderCannotBePlacedException.becauseHasNoShippingInfo(this.getId());
        }
        if (isNull(this.getPaymentMethod())) {
            throw OrderCannotBePlacedException.becauseHasNoPaymentMethod(this.getId());
        }
        if (this.getItems().isEmpty()) {
            throw OrderCannotBePlacedException.becauseHasNoOrderItems(this.getId());
        }
    }

    private void verifyIfChangeable() {
        if (!this.isDraft()) {
            throw new OrderCannotBeEditedException(this.getId(), this.getStatus());
        }
    }

    private OrderItem findOrderItem(final OrderItemId orderItemId) {
        requireNonNull(orderItemId, "orderItemId cannot be null");
        return this.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderDoesNotContainOrderItemException(this.getId(), orderItemId));
    }

    private void setId(final OrderId id) {
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

    private void setPlacedAt(final Instant placedAt) {
        this.placedAt = placedAt;
    }

    private void setPaidAt(final Instant paidAt) {
        this.paidAt = paidAt;
    }

    private void setCanceledAt(final Instant canceledAt) {
        this.canceledAt = canceledAt;
    }

    private void setReadyAt(final Instant readyAt) {
        this.readyAt = readyAt;
    }

    private void setBilling(final Billing billing) {
        this.billing = billing;
    }

    private void setShipping(final Shipping shipping) {
        this.shipping = shipping;
    }

    private void setStatus(final OrderStatus status) {
        requireNonNull(status, "status cannot be null");
        this.status = status;
    }

    private void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Set<OrderItem> getItems() {
        return Collections.unmodifiableSet(items);
    }

    private void setItems(final Set<OrderItem> items) {
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
        final Order order = (Order) o;
        return Objects.equals(this.getId(), order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

}
