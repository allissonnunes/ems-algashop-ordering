package com.github.allisson95.algashop.ordering.domain.model.service;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.entity.PaymentMethod;
import com.github.allisson95.algashop.ordering.domain.model.entity.ProductTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
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
                o -> assertThat(o.customerId()).isEqualTo(expectedCustomerId),
                o -> assertThat(o.billing()).isEqualTo(expectedBilling),
                o -> assertThat(o.shipping()).isEqualTo(expectedShipping),
                o -> assertThat(o.paymentMethod()).isEqualTo(expectedPaymentMethod),
                o -> assertThat(o.totalItems()).isEqualTo(expectedQuantity),
                o -> assertThatCollection(o.items()).hasSize(1),
                o -> assertThat(o.totalAmount()).isEqualTo(expectedTotalCost)
        );
        assertWith(order.items().iterator().next(),
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