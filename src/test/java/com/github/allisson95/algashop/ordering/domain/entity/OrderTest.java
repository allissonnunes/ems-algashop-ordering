package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.github.allisson95.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.github.allisson95.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.github.allisson95.algashop.ordering.domain.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.CustomerId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    Faker faker = new Faker();

    @Test
    void shouldGenerateNewOrder() {
        final CustomerId customerId = new CustomerId();
        final Order draftOrder = Order.draft(customerId);
        assertWith(draftOrder,
                o -> assertThat(o.id()).isNotNull(),
                o -> assertThat(o.customerId()).isEqualTo(customerId),
                o -> assertThat(o.isDraft()).isTrue(),
                o -> assertThat(o.items()).isEmpty(),
                o -> assertThat(o.totalAmount()).isEqualTo(new Money(BigDecimal.ZERO)),
                o -> assertThat(o.totalItems()).isEqualTo(new Quantity(0)),
                o -> assertThat(o.placedAt()).isNull(),
                o -> assertThat(o.paidAt()).isNull(),
                o -> assertThat(o.cancelledAt()).isNull(),
                o -> assertThat(o.readyAt()).isNull(),
                o -> assertThat(o.billing()).isNull(),
                o -> assertThat(o.shipping()).isNull(),
                o -> assertThat(o.paymentMethod()).isNull()
        );
    }

    @Test
    void shouldAddItem() {
        final Order order = Order.draft(new CustomerId());
        final Product product = ProductTestDataBuilder.aProduct().build();
        order.addItem(product, new Quantity(1));

        assertWith(order,
                o -> assertThat(o.items()).hasSize(1),
                o -> assertThat(o.totalItems()).isEqualTo(new Quantity(1)),
                o -> assertThat(o.totalAmount()).isEqualTo(product.price()),
                o -> assertWith(o.items().iterator().next(),
                        i -> assertThat(i.id()).isNotNull(),
                        i -> assertThat(i.orderId()).isEqualTo(order.id()),
                        i -> assertThat(i.productId()).isEqualTo(product.id()),
                        i -> assertThat(i.productName()).isEqualTo(product.name()),
                        i -> assertThat(i.price()).isEqualTo(product.price()),
                        i -> assertThat(i.quantity()).isEqualTo(new Quantity(1)),
                        i -> assertThat(i.totalAmount()).isEqualTo(product.price()))
        );
    }

    @Test
    void shouldReturnUnmodifiableItemsToPreventDirectChanges() {
        final Order order = Order.draft(new CustomerId());
        final Product product = ProductTestDataBuilder.aProduct().build();
        order.addItem(product, new Quantity(1));
        assertThat(order.items()).isUnmodifiable();
    }

    @Test
    void shouldCalculateTotals() {
        final Order order = Order.draft(new CustomerId());
        final Product product1 = ProductTestDataBuilder.aProduct().build();
        final Product product2 = ProductTestDataBuilder.aProduct().build();
        final Money expectedPrice = product1.price().add(product2.price().multiply(new Quantity(2)));
        final Quantity expectedQuantity = new Quantity(3);

        order.addItem(product1, new Quantity(1));
        order.addItem(product2, new Quantity(2));

        assertThat(order.totalAmount()).isEqualTo(expectedPrice);
        assertThat(order.totalItems()).isEqualTo(expectedQuantity);
    }

    @Test
    void givenDraftOrder_whenPlace_thenChangeOrderStatusToPlaced() {
        final Order order = OrderTestDataBuilder.anOrder().build();
        order.place();
        assertThat(order.isPlaced()).isTrue();
        assertThat(order.placedAt()).isNotNull();
    }

    @Test
    void givenPlacedOrder_whenTryToPlaceAgain_thenThrowException() {
        final Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(placedOrder::place)
                .withMessage("Cannot change order %s status from %s to %s".formatted(placedOrder.id(), placedOrder.status(), OrderStatus.PLACED));
    }

    @Test
    void givenDraftOrder_whenChangePaymentMethod_shouldAllowChange() {
        final Order draftOrder = Order.draft(new CustomerId());
        draftOrder.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        assertThat(draftOrder.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void givenDraftOrder_whenChangePaymentMethodToNull_shouldThrowException() {
        final Order draftOrder = Order.draft(new CustomerId());
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> draftOrder.changePaymentMethod(null))
                .withMessage("newPaymentMethod cannot be null");
    }

    @Test
    void givenDraftOrder_whenChangeBilling_shouldAllowChange() {
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Order draftOrder = Order.draft(new CustomerId());

        draftOrder.changeBillingInfo(billing);

        assertThat(draftOrder.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrder_whenChangeShipping_shouldAllowedChange() {
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final Order draftOrder = Order.draft(new CustomerId());

        draftOrder.changeShipping(shipping);

        assertWith(draftOrder,
                o -> assertThat(o.shipping()).isEqualTo(shipping)
        );
    }

    @Test
    void givenDraftOrderAndExpectedDeliveryDateInThePast_whenChangeShippingInfo_shouldNotAllowedChange() {
        final Shipping shipping = OrderTestDataBuilder.aShipping().toBuilder()
                .expectedDeliveryDate(LocalDate.now().minusDays(faker.number().numberBetween(1, 10)))
                .build();
        final Order draftOrder = Order.draft(new CustomerId());

        assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(() -> draftOrder.changeShipping(shipping))
                .withMessage("Order %s expected delivery date must be after current date".formatted(draftOrder.id()));
    }

    @Test
    void givenDraftOrder_whenChangeItemQuantity_shouldBeRecalculated() {
        final Order order = Order.draft(new CustomerId());
        final Product product = ProductTestDataBuilder.aProduct().build();
        order.addItem(product, new Quantity(1));
        final Money expectedPrice = product.price().multiply(new Quantity(10));

        final OrderItem orderItem = order.items().iterator().next();
        order.changeItemQuantity(orderItem.id(), new Quantity(10));

        assertWith(order,
                o -> assertThat(o.totalAmount()).isEqualTo(expectedPrice),
                o -> assertThat(o.totalItems()).isEqualTo(new Quantity(10))
        );
    }

    @Test
    void givenOutOfStockProduct_whenTryToAddToOrder_shouldThrowException() {
        final Order order = OrderTestDataBuilder.anOrder().build();
        final Product outOfStockProduct = ProductTestDataBuilder.anOutOfStockProduct().build();

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> order.addItem(outOfStockProduct, new Quantity(1)))
                .withMessage("Product %s out of stock".formatted(outOfStockProduct.id()));
    }

}