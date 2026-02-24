package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderChangingTest {

    @ParameterizedTest
    @EnumSource
    void givenOrderWithStatusDifferentFromDraft_whenUpdate_shouldThrowException(final OrderStatus status) {
        final Order order = OrderTestDataBuilder.anOrder().status(status).build();
        final Billing newBilling = OrderTestDataBuilder.aBilling();
        final Shipping newShipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod newPaymentMethod = PaymentMethod.CREDIT_CARD;
        final CreditCardId creditCardId = new CreditCardId();
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(1);
        final OrderItem orderItem = order.getItems().iterator().next();
        final OrderItemId orderItemId = orderItem.getId();
        final Quantity newQuantity = new Quantity(2);

        final List<ThrowableAssert.ThrowingCallable> tasks = List.of(
                () -> order.changeBilling(newBilling),
                () -> order.changeShipping(newShipping),
                () -> order.changePaymentMethod(newPaymentMethod, creditCardId),
                () -> order.addItem(product, quantity),
                () -> order.changeItemQuantity(orderItemId, newQuantity),
                () -> order.removeItem(orderItemId)
        );

        tasks.forEach(task -> {
            if (OrderStatus.DRAFT.equals(status)) {
                assertThatCode(task).doesNotThrowAnyException();
            } else {
                assertThatThrownBy(task).isInstanceOf(OrderCannotBeEditedException.class);
            }
        });
    }

}
