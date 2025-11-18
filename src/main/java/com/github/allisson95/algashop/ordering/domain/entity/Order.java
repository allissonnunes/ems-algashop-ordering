package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.CustomerId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ProductId;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Order {

    private OrderId id;

    private CustomerId customerId;

    private Money totalAmount;

    private Quantity totalItems;

    private Instant placedAt;

    private Instant paidAt;

    private Instant cancelledAt;

    private Instant readyAt;

    private BillingInfo billing;

    private ShippingInfo shipping;

    private Money shippingCoast;

    private LocalDate expectedDeliveryDate;

    private OrderStatus status;

    private PaymentMethod paymentMethod;

    private Set<OrderItem> items;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existingOrder")
    private Order(final OrderId id, final CustomerId customerId, final Money totalAmount, final Quantity totalItems, final Instant placedAt, final Instant paidAt, final Instant cancelledAt, final Instant readyAt, final BillingInfo billing, final ShippingInfo shipping, final Money shippingCoast, final LocalDate expectedDeliveryDate, final OrderStatus status, final PaymentMethod paymentMethod, final Set<OrderItem> items) {
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
        this.setShippingCoast(shippingCoast);
        this.setExpectedDeliveryDate(expectedDeliveryDate);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setItems(items);
    }

    public static Order draft(final CustomerId customerId) {
        return new Order(new OrderId(), customerId, Money.ZERO, Quantity.ZERO, null, null, null, null, null, null, null, null, OrderStatus.DRAFT, null, new HashSet<>());
    }

    public void addItem(final ProductId productId, final ProductName productName, final Money price, final Quantity quantity) {
        final OrderItem orderItem = OrderItem.newOrderItem()
                .orderId(this.id())
                .productId(productId)
                .productName(productName)
                .price(price)
                .quantity(quantity)
                .build();

        if (this.items == null) {
            this.setItems(new HashSet<>());
        }

        this.items.add(orderItem);
        this.recalculateTotals();
    }

    private void recalculateTotals() {
        final Money shippingCost = Objects.requireNonNullElse(this.shippingCoast(), Money.ZERO);
        final Money totalItemsAmount = this.items().stream().map(OrderItem::totalAmount).reduce(Money.ZERO, Money::add);
        final Quantity totalItemsCount = this.items().stream().map(OrderItem::quantity).reduce(Quantity.ZERO, Quantity::add);

        final Money totalOrderAmount = totalItemsAmount.add(shippingCost);

        this.setTotalAmount(totalOrderAmount);
        this.setTotalItems(totalItemsCount);
    }

    public OrderId id() {
        return id;
    }

    private void setId(final OrderId id) {
        Objects.requireNonNull(id, "id cannot be null");
        this.id = id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    private void setCustomerId(final CustomerId customerId) {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        this.customerId = customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    private void setTotalAmount(final Money totalAmount) {
        Objects.requireNonNull(totalAmount, "totalAmount cannot be null");
        this.totalAmount = totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    private void setTotalItems(final Quantity totalItems) {
        Objects.requireNonNull(totalItems, "totalItems cannot be null");
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

    public BillingInfo billing() {
        return billing;
    }

    private void setBilling(final BillingInfo billing) {
        this.billing = billing;
    }

    public ShippingInfo shipping() {
        return shipping;
    }

    private void setShipping(final ShippingInfo shipping) {
        this.shipping = shipping;
    }

    public Money shippingCoast() {
        return shippingCoast;
    }

    private void setShippingCoast(final Money shippingCoast) {
        this.shippingCoast = shippingCoast;
    }

    public LocalDate expectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    private void setExpectedDeliveryDate(final LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public OrderStatus status() {
        return status;
    }

    private void setStatus(final OrderStatus status) {
        Objects.requireNonNull(status, "status cannot be null");
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
        Objects.requireNonNull(items, "items cannot be null");
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
