package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;

import static java.util.Objects.requireNonNull;

public class OrderFactory {

    private OrderFactory() {
    }

    public static Order filled(final CustomerId customerId,
                               final Billing billing,
                               final Shipping shipping,
                               final PaymentMethod paymentMethod,
                               final CreditCardId creditCardId,
                               final Product product,
                               final Quantity productQuantity) {
        requireNonNull(customerId, "customerId cannot be null");
        requireNonNull(shipping, "shipping cannot be null");
        requireNonNull(billing, "billing cannot be null");
        requireNonNull(paymentMethod, "paymentMethod cannot be null");
        requireNonNull(product, "product cannot be null");
        requireNonNull(productQuantity, "productQuantity cannot be null");

        final Order order = Order.draft(customerId);
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod, creditCardId);
        order.addItem(product, productQuantity);

        return order;
    }

}
