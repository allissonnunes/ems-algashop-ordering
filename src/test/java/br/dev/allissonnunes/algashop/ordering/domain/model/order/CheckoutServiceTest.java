package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.LoyaltyPoints;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private Orders orders;

    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        var specification = new CustomerHaveFreeShippingSpecification(
                orders,
                2L,
                new LoyaltyPoints(100),
                new LoyaltyPoints(2000)
        );

        checkoutService = new CheckoutService(specification);
    }

    @Test
    void shouldCheckout() {
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        final CreditCardId creditCardId = new CreditCardId();

        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        final CustomerId customerId = customer.getId();
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        final Set<ShoppingCartItem> shoppingCartItems = new LinkedHashSet<>(shoppingCart.getItems());

        final Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod, creditCardId);

        assertWith(order,
                o -> assertThat(o.getBilling()).isEqualTo(billing),
                o -> assertThat(o.getShipping()).isEqualTo(shipping),
                o -> assertThat(o.getPaymentMethod()).isEqualTo(paymentMethod),
                o -> assertThat(o.getCustomerId()).isEqualTo(customerId),
                o -> assertThat(o.getStatus()).isEqualTo(OrderStatus.PLACED)
        );
        assertThatCollection(order.getItems())
                .extracting(OrderItem::getProductId, OrderItem::getProductName, OrderItem::getPrice, OrderItem::getQuantity, OrderItem::getTotalAmount)
                .contains(shoppingCartItems.stream()
                        .map(item -> tuple(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity(), item.getTotalAmount()))
                        .toArray(Tuple[]::new));
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCheckoutShoppingCartWithUnavailableProducts() {
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        final CreditCardId creditCardId = new CreditCardId();

        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        final CustomerId customerId = customer.getId();
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        final ShoppingCartItem shoppingCartItem = shoppingCart.getItems().iterator().next();
        final Product unavailableProduct = ProductTestDataBuilder.anOutOfStockProduct()
                .id(shoppingCartItem.getProductId())
                .name(shoppingCartItem.getProductName())
                .price(shoppingCartItem.getPrice())
                .build();
        shoppingCart.refreshItem(unavailableProduct);

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod, creditCardId));
        assertThat(shoppingCart.isEmpty()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenCheckoutShoppingCartIsEmpty() {
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        final CreditCardId creditCardId = new CreditCardId();

        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        final CustomerId customerId = customer.getId();
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        shoppingCart.empty();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod, creditCardId));
        assertThat(shoppingCart.isEmpty()).isTrue();
    }


    @Test
    void givenValidShoppingCartAndCustomerWithFreeShipping_whenCheckout_shouldCreateOrderWithFreeShipping() {
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        final CreditCardId creditCardId = new CreditCardId();

        final Customer customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(3000)).build();
        final CustomerId customerId = customer.getId();
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        final Set<ShoppingCartItem> shoppingCartItems = new LinkedHashSet<>(shoppingCart.getItems());

        final Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod, creditCardId);

        assertWith(order,
                o -> assertThat(o.getBilling()).isEqualTo(billing),
                o -> assertThat(o.getShipping()).isEqualTo(shipping.toBuilder().cost(Money.ZERO).build()),
                o -> assertThat(o.getPaymentMethod()).isEqualTo(paymentMethod),
                o -> assertThat(o.getCreditCardId()).isEqualTo(creditCardId),
                o -> assertThat(o.getCustomerId()).isEqualTo(customerId),
                o -> assertThat(o.getStatus()).isEqualTo(OrderStatus.PLACED)
        );
        assertThatCollection(order.getItems())
                .extracting(OrderItem::getProductId, OrderItem::getProductName, OrderItem::getPrice, OrderItem::getQuantity, OrderItem::getTotalAmount)
                .contains(shoppingCartItems.stream()
                        .map(item -> tuple(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity(), item.getTotalAmount()))
                        .toArray(Tuple[]::new));
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

}