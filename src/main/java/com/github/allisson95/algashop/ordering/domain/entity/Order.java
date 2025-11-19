package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.*;
import com.github.allisson95.algashop.ordering.domain.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.CustomerId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderItemId;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class Order {

    private OrderId id;

    private CustomerId customerId;

    private Money totalAmount;

    private Quantity totalItems;

    private Instant placedAt;

    private Instant paidAt;

    private Instant cancelledAt;

    private Instant readyAt;

    private Billing billing;

    private Shipping shipping;

    private OrderStatus status;

    private PaymentMethod paymentMethod;

    private Set<OrderItem> items;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existingOrder")
    private Order(final OrderId id, final CustomerId customerId, final Money totalAmount, final Quantity totalItems, final Instant placedAt, final Instant paidAt, final Instant cancelledAt, final Instant readyAt, final Billing billing, final Shipping shipping, final OrderStatus status, final PaymentMethod paymentMethod, final Set<OrderItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCancelledAt(cancelledAt);
        this.setReadyAt(readyAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setItems(items);
    }

    public static Order draft(final CustomerId customerId) {
        return new Order(new OrderId(), customerId, Money.ZERO, Quantity.ZERO, null, null, null, null, null, null, OrderStatus.DRAFT, null, new HashSet<>());
    }

    public void markAsPaid() {
        this.changeStatus(OrderStatus.PAID);
        this.setPaidAt(Instant.now());
    }

    public void markAsReady() {
        this.changeStatus(OrderStatus.READY);
        this.setReadyAt(Instant.now());
    }

    public void cancel() {
        this.changeStatus(OrderStatus.CANCELED);
        this.setCancelledAt(Instant.now());
    }

    public void place() {
        this.verifyIfCanChangeToPlaced();
        this.changeStatus(OrderStatus.PLACED);
        this.setPlacedAt(Instant.now());
    }

    public boolean isPaid() {
        return OrderStatus.PAID.equals(this.status());
    }

    public boolean isCanceled() {
        return OrderStatus.CANCELED.equals(this.status());
    }

    public boolean isReady() {
        return OrderStatus.READY.equals(this.status());
    }

    public boolean isDraft() {
        return OrderStatus.DRAFT.equals(this.status());
    }

    public boolean isPlaced() {
        return OrderStatus.PLACED.equals(this.status());
    }

    public void addItem(final Product product, final Quantity quantity) {
        requireNonNull(product, "product cannot be null");
        requireNonNull(quantity, "quantity cannot be null");

        this.verifyIfChangeable();

        product.checkOutOfStock();

        final OrderItem orderItem = OrderItem.newOrderItem()
                .orderId(this.id())
                .product(product)
                .quantity(quantity)
                .build();

        if (this.items == null) {
            this.setItems(new HashSet<>());
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
            throw new OrderInvalidShippingDeliveryDateException(this.id());
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
        if (this.status().cantBeUpdatedTo(newStatus)) {
            throw new OrderStatusCannotBeChangedException(this.id(), this.status(), newStatus);
        }
        this.setStatus(newStatus);
    }

    private void recalculateTotals() {
        final Money shippingCost = Optional.ofNullable(this.shipping()).map(Shipping::cost).orElse(Money.ZERO);
        final Money totalItemsAmount = this.items().stream().map(OrderItem::totalAmount).reduce(Money.ZERO, Money::add);
        final Quantity totalItemsCount = this.items().stream().map(OrderItem::quantity).reduce(Quantity.ZERO, Quantity::add);

        final Money totalOrderAmount = totalItemsAmount.add(shippingCost);

        this.setTotalAmount(totalOrderAmount);
        this.setTotalItems(totalItemsCount);
    }

    private void verifyIfCanChangeToPlaced() {
        if (isNull(this.billing())) {
            throw OrderCannotBePlacedException.becauseHasNoBillingInfo(this.id());
        }
        if (isNull(this.shipping())) {
            throw OrderCannotBePlacedException.becauseHasNoShippingInfo(this.id());
        }
        if (isNull(this.paymentMethod())) {
            throw OrderCannotBePlacedException.becauseHasNoPaymentMethod(this.id());
        }
        if (this.items().isEmpty()) {
            throw OrderCannotBePlacedException.becauseHasNoOrderItems(this.id());
        }
    }

    private void verifyIfChangeable() {
        if (!this.isDraft()) {
            throw new OrderCannotBeEditedException(this.id(), this.status());
        }
    }

    private OrderItem findOrderItem(final OrderItemId orderItemId) {
        requireNonNull(orderItemId, "orderItemId cannot be null");
        return this.items().stream()
                .filter(item -> item.id().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderDoesNotContainOrderItemException(this.id(), orderItemId));
    }

    public OrderId id() {
        return id;
    }

    private void setId(final OrderId id) {
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

    public Instant placedAt() {
        return placedAt;
    }

    private void setPlacedAt(final Instant placedAt) {
        this.placedAt = placedAt;
    }

    public Instant paidAt() {
        return paidAt;
    }

    private void setPaidAt(final Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant cancelledAt() {
        return cancelledAt;
    }

    private void setCancelledAt(final Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Instant readyAt() {
        return readyAt;
    }

    private void setReadyAt(final Instant readyAt) {
        this.readyAt = readyAt;
    }

    public Billing billing() {
        return billing;
    }

    private void setBilling(final Billing billing) {
        this.billing = billing;
    }

    public Shipping shipping() {
        return shipping;
    }

    private void setShipping(final Shipping shipping) {
        this.shipping = shipping;
    }

    public OrderStatus status() {
        return status;
    }

    private void setStatus(final OrderStatus status) {
        requireNonNull(status, "status cannot be null");
        this.status = status;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    private void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(items);
    }

    private void setItems(final Set<OrderItem> items) {
        requireNonNull(items, "items cannot be null");
        this.items = items;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Order order = (Order) o;
        return Objects.equals(id(), order.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

}
