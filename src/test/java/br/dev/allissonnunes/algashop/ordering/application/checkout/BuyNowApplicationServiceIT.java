package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.AbstractApplicationIT;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Orders;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductCatalogService;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BuyNowApplicationServiceIT extends AbstractApplicationIT {

    @Autowired
    private BuyNowApplicationService buyNowApplicationService;

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @BeforeEach
    public void setup() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    public void shouldBuyNow() {
        final Product product = ProductTestDataBuilder.aProduct().build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
        when(shippingCostService.calculate(any(ShippingCostService.CalculationRequest.class)))
                .thenReturn(new CalculationResponse(new Money("10.00"), LocalDate.now().plusDays(3)));

        final BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();

        final String orderId = buyNowApplicationService.buyNow(input);

        final Order retrievedOrder = orders.ofId(new OrderId(orderId)).orElseThrow();

        assertWith(retrievedOrder,
                o -> assertThat(o.getCustomerId().value()).isEqualTo(input.customerId()),
                o -> assertThat(o.getPaymentMethod().name()).isEqualTo(input.paymentMethod()),

//                Recipient recipient, Address address, Money cost, LocalDate expectedDeliveryDate
                o -> assertThat(o.getShipping().recipient().fullName().firstName()).isEqualTo(input.shipping().recipient().firstName()),
                o -> assertThat(o.getShipping().recipient().fullName().lastName()).isEqualTo(input.shipping().recipient().lastName()),
                o -> assertThat(o.getShipping().recipient().document().value()).isEqualTo(input.shipping().recipient().document()),
                o -> assertThat(o.getShipping().recipient().phone().value()).isEqualTo(input.shipping().recipient().phone()),
                o -> assertThat(o.getShipping().cost()).isEqualTo(new Money("10.00")),
                o -> assertThat(o.getShipping().expectedDeliveryDate()).isEqualTo(LocalDate.now().plusDays(3)),

//                FullName fullName, Document document, Phone phone, Email email, Address address
                o -> assertThat(o.getBilling().fullName().firstName()).isEqualTo(input.billing().firstName()),
                o -> assertThat(o.getBilling().fullName().lastName()).isEqualTo(input.billing().lastName()),
                o -> assertThat(o.getBilling().document().value()).isEqualTo(input.billing().document()),
                o -> assertThat(o.getBilling().phone().value()).isEqualTo(input.billing().phone()),
                o -> assertThat(o.getBilling().email().value()).isEqualTo(input.billing().email())
        );
    }

}