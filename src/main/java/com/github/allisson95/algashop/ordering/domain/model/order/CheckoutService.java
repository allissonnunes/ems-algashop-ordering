package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.DomainService;
import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@DomainService
@RequiredArgsConstructor
public class CheckoutService {

    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;

    public Order checkout(final Customer customer, final ShoppingCart shoppingCart, final Billing billing, final Shipping shipping, final PaymentMethod paymentMethod) {
        requireNonNull(customer, "Customer cannot be null");
        requireNonNull(shoppingCart, "Shopping cart cannot be null");
        requireNonNull(billing, "Billing information cannot be null");
        requireNonNull(shipping, "Shipping information cannot be null");
        requireNonNull(paymentMethod, "Payment method cannot be null");

        if (shoppingCart.isEmpty() || shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException();
        }

        final Order newOrder = Order.draft(shoppingCart.getCustomerId());
        newOrder.changeBilling(billing);

        if (haveFreeShipping(customer)) {
            newOrder.changeShipping(shipping.toBuilder().cost(Money.ZERO).build());
        } else {
            newOrder.changeShipping(shipping);
        }

        newOrder.changePaymentMethod(paymentMethod);

        for (final ShoppingCartItem sci : shoppingCart.getItems()) {
            final Product product = getProduct(sci);
            newOrder.addItem(product, sci.getQuantity());
        }

        newOrder.place();

        shoppingCart.empty();

        return newOrder;
    }

    private boolean haveFreeShipping(final Customer customer) {
        return customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }

    private Product getProduct(final ShoppingCartItem sci) {
        return Product.builder()
                .id(sci.getProductId())
                .name(sci.getProductName())
                .price(sci.getPrice())
                .inStock(sci.getAvailable())
                .build();
    }

}
