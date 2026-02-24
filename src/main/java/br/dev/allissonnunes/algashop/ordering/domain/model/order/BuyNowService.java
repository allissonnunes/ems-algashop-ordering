package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainService;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@DomainService
@RequiredArgsConstructor
public class BuyNowService {

    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;

    public Order buyNow(final Product product,
                        final Customer customer,
                        final Billing billing,
                        final Shipping shipping,
                        final Quantity quantity,
                        final PaymentMethod paymentMethod,
                        final CreditCardId creditCardId) {
        requireNonNull(product, "product cannot be null");
        requireNonNull(customer, "customer cannot be null");
        requireNonNull(billing, "billing cannot be null");
        requireNonNull(shipping, "shipping cannot be null");
        requireNonNull(quantity, "quantity cannot be null");
        requireNonNull(paymentMethod, "paymentMethod cannot be null");

        if (PaymentMethod.CREDIT_CARD.equals(paymentMethod)) {
            requireNonNull(creditCardId, "creditCardId cannot be null");
        }

        product.checkOutOfStock();

        if (Quantity.ZERO.equals(quantity)) {
            throw new IllegalArgumentException("quantity cannot be zero");
        }

        final Order newOrder = Order.draft(customer.getId());
        newOrder.changeBilling(billing);

        if (haveFreeShipping(customer)) {
            newOrder.changeShipping(shipping.toBuilder().cost(Money.ZERO).build());
        } else {
            newOrder.changeShipping(shipping);
        }

        newOrder.changePaymentMethod(paymentMethod, creditCardId);
        newOrder.addItem(product, quantity);

        newOrder.place();

        return newOrder;
    }

    private boolean haveFreeShipping(final Customer customer) {
        return customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }

}
