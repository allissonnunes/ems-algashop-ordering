package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderStatus;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CustomerLoyaltyPointsServiceTest {

    private final CustomerLoyaltyPointsService service = new CustomerLoyaltyPointsService();

    @Test
    void shouldAddPointsToTheCustomerIfOrderIsReady() {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        final Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();

        service.addPoints(customer, order);

        assertThat(customer.getLoyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void shouldThrowExceptionIfTheOrderDoesNotBelongToTheCustomer() {
        final Customer customer = CustomerTestDataBuilder.newCustomer().build();
        final Order order = OrderTestDataBuilder.anOrder().build();

        assertThatExceptionOfType(OrderNotBelongsToCustomerException.class)
                .isThrownBy(() -> service.addPoints(customer, order));
    }

    @ParameterizedTest
    @EnumSource(names = { "READY" }, mode = EnumSource.Mode.EXCLUDE)
    void shouldThrowExceptionIfTheOrderIsNotReady(final OrderStatus orderStatus) {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        final Order order = OrderTestDataBuilder.anOrder().status(orderStatus).build();

        assertThatExceptionOfType(CantAddLoyaltyPointsIfOrderIsNotReady.class)
                .isThrownBy(() -> service.addPoints(customer, order));
    }

    @Test
    void shouldNotAddPointsToTheCustomerIfOrderDoesNotHaveEnoughValueToEarnPoints() {
        final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        final Product lowCostProduct = ProductTestDataBuilder.aProduct().price(new Money("200")).build();
        final Order order = OrderTestDataBuilder.anOrder().withItems(false).status(OrderStatus.DRAFT).build();
        order.addItem(lowCostProduct, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        service.addPoints(customer, order);

        assertThat(customer.getLoyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }

}