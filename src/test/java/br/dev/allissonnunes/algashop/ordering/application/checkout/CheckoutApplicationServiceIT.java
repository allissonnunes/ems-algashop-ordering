package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.AbstractApplicationIT;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.*;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.*;
import br.dev.allissonnunes.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutApplicationServiceIT extends AbstractApplicationIT {

    @Autowired
    private CheckoutApplicationService service;

    @Autowired
    private Orders orders;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @BeforeEach
    public void setup() {
        when(shippingCostService.calculate(any(ShippingCostService.CalculationRequest.class)))
                .thenReturn(new ShippingCostService.CalculationResponse(
                        new Money("10.00"),
                        LocalDate.now().plusDays(3)
                ));

        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldCheckout() {
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCarts.add(shoppingCart);

        CheckoutInput input = CheckoutInputTestDataBuilder.aCheckoutInput()
                .shoppingCartId(shoppingCart.getId().value())
                .build();


        String orderId = service.checkout(input);

        assertThat(orderId).isNotBlank();
        assertThat(orders.exists(new OrderId(orderId))).isTrue();

        Optional<Order> createdOrder = orders.ofId(new OrderId(orderId));
        assertThat(createdOrder).isPresent();
        assertThat(createdOrder.get().getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(createdOrder.get().getTotalAmount().value()).isGreaterThan(BigDecimal.ZERO);

        Optional<ShoppingCart> updatedCart = shoppingCarts.ofId(shoppingCart.getId());
        assertThat(updatedCart).isPresent();
        assertThat(updatedCart.get().isEmpty()).isTrue();

        verify(orderEventListener, times(1)).handleOrderPlacedEvent(any(OrderPlacedEvent.class));
    }

    @Test
    void shouldThrowShoppingCartNotFoundExceptionWhenCheckoutWithNonExistingShoppingCart() {
        CheckoutInput input = CheckoutInputTestDataBuilder.aCheckoutInput()
                .shoppingCartId(UUID.randomUUID())
                .build();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.checkout(input));
    }

    @Test
    void shouldThrowShoppingCartCantProceedToCheckoutExceptionWhenCartIsEmpty() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);

        CheckoutInput input = CheckoutInputTestDataBuilder.aCheckoutInput()
                .shoppingCartId(shoppingCart.getId().value())
                .build();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> service.checkout(input));
    }

    @Test
    void shouldThrowShoppingCartCantProceedToCheckoutExceptionWhenCartContainsUnavailableItems() {
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        Product unavailableProduct = ProductTestDataBuilder.aProduct().id(product.id()).inStock(false).build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.refreshItem(unavailableProduct);
        shoppingCarts.add(shoppingCart);

        CheckoutInput input = CheckoutInputTestDataBuilder.aCheckoutInput()
                .shoppingCartId(shoppingCart.getId().value())
                .build();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> service.checkout(input));
    }

}