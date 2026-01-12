package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductOutOfStockException;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    Faker faker = new Faker();

    @Test
    void shouldGenerateDraftOrder() {
        final CustomerId customerId = new CustomerId();
        final Order draftOrder = Order.draft(customerId);
        assertWith(draftOrder,
                o -> assertThat(o.getId()).isNotNull(),
                o -> assertThat(o.getCustomerId()).isEqualTo(customerId),
                o -> assertThat(o.isDraft()).isTrue(),
                o -> assertThat(o.getItems()).isEmpty(),
                o -> assertThat(o.getTotalAmount()).isEqualTo(new Money(BigDecimal.ZERO)),
                o -> assertThat(o.getTotalItems()).isEqualTo(new Quantity(0)),
                o -> assertThat(o.getPlacedAt()).isNull(),
                o -> assertThat(o.getPaidAt()).isNull(),
                o -> assertThat(o.getCanceledAt()).isNull(),
                o -> assertThat(o.getReadyAt()).isNull(),
                o -> assertThat(o.getBilling()).isNull(),
                o -> assertThat(o.getShipping()).isNull(),
                o -> assertThat(o.getPaymentMethod()).isNull()
        );
    }

    @Test
    void shouldAddItem() {
        final Order order = Order.draft(new CustomerId());
        final Product product = ProductTestDataBuilder.aProduct().build();
        order.addItem(product, new Quantity(1));

        assertWith(order,
                o -> assertThat(o.getItems()).hasSize(1),
                o -> assertThat(o.getTotalItems()).isEqualTo(new Quantity(1)),
                o -> assertThat(o.getTotalAmount()).isEqualTo(product.price()),
                o -> assertWith(o.getItems().iterator().next(),
                        i -> assertThat(i.getId()).isNotNull(),
                        i -> assertThat(i.getOrderId()).isEqualTo(order.getId()),
                        i -> assertThat(i.getProductId()).isEqualTo(product.id()),
                        i -> assertThat(i.getProductName()).isEqualTo(product.name()),
                        i -> assertThat(i.getPrice()).isEqualTo(product.price()),
                        i -> assertThat(i.getQuantity()).isEqualTo(new Quantity(1)),
                        i -> assertThat(i.getTotalAmount()).isEqualTo(product.price()))
        );
    }

    @Test
    void shouldReturnUnmodifiableItemsToPreventDirectChanges() {
        final Order order = Order.draft(new CustomerId());
        final Product product = ProductTestDataBuilder.aProduct().build();
        order.addItem(product, new Quantity(1));
        assertThat(order.getItems()).isUnmodifiable();
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

        assertThat(order.getTotalAmount()).isEqualTo(expectedPrice);
        assertThat(order.getTotalItems()).isEqualTo(expectedQuantity);
    }

    @Test
    void givenDraftOrder_whenPlace_thenChangeOrderStatusToPlaced() {
        final Order order = OrderTestDataBuilder.anOrder().build();
        order.place();
        assertThat(order.isPlaced()).isTrue();
        assertThat(order.getPlacedAt()).isNotNull();
    }

    @Test
    void givenPlacedOrder_whenTryToPlaceAgain_thenThrowException() {
        final Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(placedOrder::place)
                .withMessage("Cannot change order %s status from %s to %s".formatted(placedOrder.getId(), placedOrder.getStatus(), OrderStatus.PLACED));
    }

    @Test
    void givenDraftOrder_whenChangePaymentMethod_shouldAllowChange() {
        final Order draftOrder = Order.draft(new CustomerId());
        draftOrder.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        assertThat(draftOrder.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
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

        draftOrder.changeBilling(billing);

        assertThat(draftOrder.getBilling()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrder_whenChangeShipping_shouldAllowedChange() {
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final Order draftOrder = Order.draft(new CustomerId());
        final Money expectedTotalAmount = draftOrder.getTotalAmount().add(shipping.cost());

        draftOrder.changeShipping(shipping);

        assertWith(draftOrder,
                o -> assertThat(o.getShipping()).isEqualTo(shipping),
                o -> assertThat(o.getTotalAmount()).isEqualTo(expectedTotalAmount)
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
                .withMessage("Order %s expected delivery date must be after current date".formatted(draftOrder.getId()));
    }

    @Test
    void givenDraftOrder_whenChangeItemQuantity_shouldBeRecalculated() {
        final Order order = Order.draft(new CustomerId());
        final Product product = ProductTestDataBuilder.aProduct().build();
        order.addItem(product, new Quantity(1));
        final Money expectedPrice = product.price().multiply(new Quantity(10));

        final OrderItem orderItem = order.getItems().iterator().next();
        order.changeItemQuantity(orderItem.getId(), new Quantity(10));

        assertWith(order,
                o -> assertThat(o.getTotalAmount()).isEqualTo(expectedPrice),
                o -> assertThat(o.getTotalItems()).isEqualTo(new Quantity(10))
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