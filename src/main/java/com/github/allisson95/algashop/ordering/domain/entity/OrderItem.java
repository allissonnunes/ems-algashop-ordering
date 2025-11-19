package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.valueobject.ProductName;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderItemId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ProductId;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class OrderItem {

    private OrderItemId id;

    private OrderId orderId;

    private ProductId productId;

    private ProductName productName;

    private Money price;

    private Quantity quantity;

    private Money totalAmount;

    @Builder(builderClassName = "ExistingOrderItemBuilder", builderMethodName = "existingOrderItem")
    private OrderItem(final OrderItemId id, final OrderId orderId, final ProductId productId, final ProductName productName, final Money price, final Quantity quantity, final Money totalAmount) {
        this.setId(id);
        this.setOrderId(orderId);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setTotalAmount(totalAmount);
    }

    @Builder(builderClassName = "NewOrderItemBuilder", builderMethodName = "newOrderItem", access = AccessLevel.PACKAGE)
    private static OrderItem createNew(final OrderId orderId, final Product product, final Quantity quantity) {
        requireNonNull(orderId, "orderId cannot be null");
        requireNonNull(product, "product cannot be null");
        requireNonNull(quantity, "quantity cannot be null");

        final OrderItem orderItem = new OrderItem(new OrderItemId(), orderId, product.id(), product.name(), product.price(), quantity, Money.ZERO);

        orderItem.recalculateTotalAmount();

        return orderItem;
    }

    void changeQuantity(final Quantity newQuantity) {
        requireNonNull(newQuantity, "newQuantity cannot be null");
        this.setQuantity(newQuantity);
        this.recalculateTotalAmount();
    }

    private void recalculateTotalAmount() {
        this.setTotalAmount(this.price().multiply(this.quantity()));
    }

    public OrderItemId id() {
        return id;
    }

    private void setId(final OrderItemId id) {
        requireNonNull(id, "id cannot be null");
        this.id = id;
    }

    public OrderId orderId() {
        return orderId;
    }

    private void setOrderId(final OrderId orderId) {
        requireNonNull(orderId, "orderId cannot be null");
        this.orderId = orderId;
    }

    public ProductId productId() {
        return productId;
    }

    private void setProductId(final ProductId productId) {
        requireNonNull(productId, "productId cannot be null");
        this.productId = productId;
    }

    public ProductName productName() {
        return productName;
    }

    private void setProductName(final ProductName productName) {
        requireNonNull(productName, "productName cannot be null");
        this.productName = productName;
    }

    public Money price() {
        return price;
    }

    private void setPrice(final Money price) {
        requireNonNull(price, "price cannot be null");
        this.price = price;
    }

    public Quantity quantity() {
        return quantity;
    }

    private void setQuantity(final Quantity quantity) {
        requireNonNull(quantity, "quantity cannot be null");
        this.quantity = quantity;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    private void setTotalAmount(final Money totalAmount) {
        requireNonNull(totalAmount, "totalAmount cannot be null");
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id(), orderItem.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

}
