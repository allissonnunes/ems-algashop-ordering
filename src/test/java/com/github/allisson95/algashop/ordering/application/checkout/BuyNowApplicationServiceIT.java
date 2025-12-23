package com.github.allisson95.algashop.ordering.application.checkout;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customers;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderId;
import com.github.allisson95.algashop.ordering.domain.model.order.Orders;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductCatalogService;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(DataJpaCleanUpExtension.class)
class BuyNowApplicationServiceIT {

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

        assertThat(orderId).isNotBlank();
        assertThat(orders.exists(new OrderId(orderId))).isTrue();
    }

}