package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderFactoryTest {

    @Test
    void shouldGenerateFilledOrder() {
        final CustomerId customerId = new CustomerId();
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity productQuantity = new Quantity(1);

        final Order filledOrder = OrderFactory.filled(customerId, billing, shipping, paymentMethod, product, productQuantity);

        assertWith(filledOrder,
                o -> assertThat(o.getCustomerId()).isEqualTo(customerId),
                o -> assertThat(o.getBilling()).isEqualTo(billing),
                o -> assertThat(o.getShipping()).isEqualTo(shipping),
                o -> assertThat(o.getPaymentMethod()).isEqualTo(paymentMethod),
                o -> assertThat(o.getTotalItems()).isEqualTo(new Quantity(1)),
                o -> assertThat(o.isDraft()).isTrue()
        );
    }

}