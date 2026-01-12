package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.LoyaltyPoints;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductOutOfStockException;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {

    @Mock
    private Orders orders;

    private BuyNowService buyNowService;

    @BeforeEach
    void setUp() {
        final CustomerHaveFreeShippingSpecification specification = new CustomerHaveFreeShippingSpecification(
                orders,
                2L,
                new LoyaltyPoints(100),
                new LoyaltyPoints(2000)
        );

        buyNowService = new BuyNowService(specification);
    }

    @Test
    void shouldCreateOrder() {
        final Product expectedProduct = ProductTestDataBuilder.aProduct().build();
        final Customer expectedCustomer = CustomerTestDataBuilder.existingCustomer().build();
        final CustomerId expectedCustomerId = expectedCustomer.getId();
        final Billing expectedBilling = OrderTestDataBuilder.aBilling();
        final Shipping expectedShipping = OrderTestDataBuilder.aShipping();
        final Quantity expectedQuantity = new Quantity(1);
        final PaymentMethod expectedPaymentMethod = PaymentMethod.CREDIT_CARD;
        final Money expectedTotalCost = expectedProduct.price().multiply(expectedQuantity).add(expectedShipping.cost());

        final Order order = buyNowService.buyNow(
                expectedProduct,
                expectedCustomer,
                expectedBilling,
                expectedShipping,
                expectedQuantity,
                expectedPaymentMethod
        );

        assertWith(order,
                o -> assertThat(o.isPlaced()).isTrue(),
                o -> assertThat(o.getCustomerId()).isEqualTo(expectedCustomerId),
                o -> assertThat(o.getBilling()).isEqualTo(expectedBilling),
                o -> assertThat(o.getShipping()).isEqualTo(expectedShipping),
                o -> assertThat(o.getPaymentMethod()).isEqualTo(expectedPaymentMethod),
                o -> assertThat(o.getTotalItems()).isEqualTo(expectedQuantity),
                o -> assertThatCollection(o.getItems()).hasSize(1),
                o -> assertThat(o.getTotalAmount()).isEqualTo(expectedTotalCost)
        );
        assertWith(order.getItems().iterator().next(),
                i -> assertThat(i.getProductId()).isEqualTo(expectedProduct.id()),
                i -> assertThat(i.getQuantity()).isEqualTo(expectedQuantity)
        );
    }

    @Test
    void shouldThrowExceptionIfProductIsOutOfStock() {
        final Product outOfStockProduct = ProductTestDataBuilder.anOutOfStockProduct().build();

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> buyNowService.buyNow(
                                outOfStockProduct,
                        CustomerTestDataBuilder.existingCustomer().build(),
                                OrderTestDataBuilder.aBilling(),
                                OrderTestDataBuilder.aShipping(),
                                new Quantity(1),
                                PaymentMethod.CREDIT_CARD
                        )
                );
    }

    @Test
    void shouldThrowExceptionIfQuantityIsZero() {
        final Quantity zero = Quantity.ZERO;
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> buyNowService.buyNow(
                                ProductTestDataBuilder.aProduct().build(),
                        CustomerTestDataBuilder.existingCustomer().build(),
                                OrderTestDataBuilder.aBilling(),
                                OrderTestDataBuilder.aShipping(),
                                zero,
                                PaymentMethod.CREDIT_CARD
                        )
                )
                .withMessage("quantity cannot be zero");
    }

    @Test
    void givenCustomerWithFreeShipping_whenBuyNow_shouldCreateOrderWithFreeShipping() {
        when(orders.salesQuantityByCustomerInYear(any(CustomerId.class), any(Year.class)))
                .thenReturn(2L);

        final Product expectedProduct = ProductTestDataBuilder.aProduct().build();
        final Customer expectedCustomer = CustomerTestDataBuilder.existingCustomer()
                .loyaltyPoints(new LoyaltyPoints(150))
                .build();
        final CustomerId expectedCustomerId = expectedCustomer.getId();
        final Billing expectedBilling = OrderTestDataBuilder.aBilling();
        final Shipping expectedShipping = OrderTestDataBuilder.aShipping();
        final Quantity expectedQuantity = new Quantity(1);
        final PaymentMethod expectedPaymentMethod = PaymentMethod.CREDIT_CARD;
        final Money expectedTotalCost = expectedProduct.price().multiply(expectedQuantity);

        final Order order = buyNowService.buyNow(
                expectedProduct,
                expectedCustomer,
                expectedBilling,
                expectedShipping,
                expectedQuantity,
                expectedPaymentMethod
        );

        assertThat(order.isPlaced()).isTrue();
        assertThat(order.getCustomerId()).isEqualTo(expectedCustomerId);
        assertThat(order.getBilling()).isEqualTo(expectedBilling);
        assertThat(order.getShipping()).isEqualTo(expectedShipping.toBuilder().cost(Money.ZERO).build());
        assertThat(order.getPaymentMethod()).isEqualTo(expectedPaymentMethod);
        assertThat(order.getTotalItems()).isEqualTo(expectedQuantity);
        assertThatCollection(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount()).isEqualTo(expectedTotalCost);

        final OrderItem orderItem = order.getItems().iterator().next();
        assertThat(orderItem.getProductId()).isEqualTo(expectedProduct.id());
        assertThat(orderItem.getQuantity()).isEqualTo(expectedQuantity);
    }

}