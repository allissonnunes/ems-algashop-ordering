package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BuyNowServiceTest {

    private final BuyNowService buyNowService = new BuyNowService();

    @Test
    void shouldCreateOrder() {
        final Product expectedProduct = ProductTestDataBuilder.aProduct().build();
        final CustomerId expectedCustomerId = new CustomerId();
        final Billing expectedBilling = OrderTestDataBuilder.aBilling();
        final Shipping expectedShipping = OrderTestDataBuilder.aShipping();
        final Quantity expectedQuantity = new Quantity(1);
        final PaymentMethod expectedPaymentMethod = PaymentMethod.CREDIT_CARD;
        final Money expectedTotalCost = expectedProduct.price().multiply(expectedQuantity).add(expectedShipping.cost());

        final Order order = buyNowService.buyNow(
                expectedProduct,
                expectedCustomerId,
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
                i -> assertThat(i.productId()).isEqualTo(expectedProduct.id()),
                i -> assertThat(i.quantity()).isEqualTo(expectedQuantity)
        );
    }

    @Test
    void shouldThrowExceptionIfProductIsOutOfStock() {
        final Product outOfStockProduct = ProductTestDataBuilder.anOutOfStockProduct().build();

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> buyNowService.buyNow(
                                outOfStockProduct,
                                new CustomerId(),
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
                                new CustomerId(),
                                OrderTestDataBuilder.aBilling(),
                                OrderTestDataBuilder.aShipping(),
                                zero,
                                PaymentMethod.CREDIT_CARD
                        )
                )
                .withMessage("quantity cannot be zero");
    }

}