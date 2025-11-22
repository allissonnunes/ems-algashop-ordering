package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.ShoppingCartItemIncompatibleProductException;
import com.github.allisson95.algashop.ordering.domain.valueobject.Money;
import com.github.allisson95.algashop.ordering.domain.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.valueobject.ProductName;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ProductId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class ShoppingCartItem {

    private ShoppingCartItemId id;

    private ShoppingCartId shoppingCartId;

    private ProductId productId;

    private ProductName productName;

    private Money price;

    private Quantity quantity;

    private Money totalAmount;

    private Boolean available;

    @Builder(builderClassName = "ExistingShoppingCartItemBuilder", builderMethodName = "existingShoppingCartItem")
    private ShoppingCartItem(final ShoppingCartItemId id, final ShoppingCartId shoppingCartId, final ProductId productId, final ProductName productName, final Money price, final Quantity quantity, final Money totalAmount, final Boolean available) {
        this.setId(id);
        this.setShoppingCartId(shoppingCartId);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setTotalAmount(totalAmount);
        this.setAvailable(available);
    }

    public static ShoppingCartItem brandNew(final ShoppingCartId shoppingCartId, final Product product, final Quantity quantity) {
        requireNonNull(shoppingCartId, "shoppingCartId cannot be null");
        requireNonNull(product, "product cannot be null");
        requireNonNull(quantity, "quantity cannot be null");

        final ShoppingCartItem shoppingCartItem = new ShoppingCartItem(new ShoppingCartItemId(), shoppingCartId, product.id(), product.name(), product.price(), quantity, Money.ZERO, product.inStock());
        shoppingCartItem.recalculateTotals();
        return shoppingCartItem;
    }

    void refresh(final Product product) {
        requireNonNull(product, "product cannot be null");
        this.verifyIfProductIsCompatible(product);

        this.setProductName(product.name());
        this.setPrice(product.price());
        this.setAvailable(product.inStock());
        this.recalculateTotals();
    }

    void changeQuantity(final Quantity newQuantity) {
        requireNonNull(newQuantity, "newQuantity cannot be null");
        if (Quantity.ZERO.equals(newQuantity)) {
            throw new IllegalArgumentException("newQuantity cannot be zero");
        }
        this.setQuantity(newQuantity);
        this.recalculateTotals();
    }

    private void verifyIfProductIsCompatible(final Product product) {
        if (!product.id().equals(this.productId())) {
            throw new ShoppingCartItemIncompatibleProductException(this.productId(), product.id());
        }
    }

    private void recalculateTotals() {
        this.setTotalAmount(this.price().multiply(this.quantity()));
    }

    public ShoppingCartItemId id() {
        return id;
    }

    private void setId(final ShoppingCartItemId id) {
        requireNonNull(id, "id cannot be null");
        this.id = id;
    }

    public ShoppingCartId shoppingCartId() {
        return shoppingCartId;
    }

    private void setShoppingCartId(final ShoppingCartId shoppingCartId) {
        requireNonNull(shoppingCartId, "shoppingCartId cannot be null");
        this.shoppingCartId = shoppingCartId;
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

    public Boolean isAvailable() {
        return available;
    }

    private void setAvailable(final Boolean available) {
        requireNonNull(available, "available cannot be null");
        this.available = available;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final ShoppingCartItem that = (ShoppingCartItem) o;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

}
