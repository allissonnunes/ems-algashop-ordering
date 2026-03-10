package br.dev.allissonnunes.algashop.ordering.contract.base;

import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.RestAssuredMockMvcExtension;
import br.dev.allissonnunes.algashop.ordering.core.application.order.OrderDetailOutputTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.OrderStatus;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.PaymentMethod;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.*;
import br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.web.order.OrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(MapStructTestConfiguration.class)
@WebMvcTest(OrderController.class)
@ExtendWith(RestAssuredMockMvcExtension.class)
class OrderBase {

    @MockitoBean
    private ForQueryingOrders orderQueryService;

    @MockitoBean
    private ForBuyingWithShoppingCart checkoutApplicationService;

    @MockitoBean
    private ForBuyingProduct buyNowApplicationService;

    @BeforeEach
    void setUp() {
        final String validOrderId = "01226N0640J7Q";
        when(orderQueryService.findById(eq(validOrderId)))
                .thenReturn(OrderDetailOutputTestDataBuilder.placedOrder(validOrderId).build());

        final String notFoundOrderId = "01226N0693HDH";
        when(orderQueryService.findById(eq(notFoundOrderId)))
                .thenThrow(new OrderNotFoundException(new OrderId(notFoundOrderId)));

        when(orderQueryService.filter(any(OrderFilter.class)))
                .thenAnswer(invocation -> {
                    final OrderFilter filter = invocation.getArgument(0);

                    final OrderSummaryOutput orderSummary = OrderSummaryOutput.builder()
                            .id(new OrderId("01226N0640J7Q").value().toLong())
                            .customer(CustomerMinimalOutput.builder()
                                    .id(new CustomerId().value())
                                    .firstName("John")
                                    .lastName("Doe")
                                    .email("johndoe@email.com")
                                    .document("12345")
                                    .phone("1191234564")
                                    .build())
                            .totalItems(2)
                            .totalAmount(new BigDecimal("41.98"))
                            .placedAt(Instant.now())
                            .paidAt(null)
                            .canceledAt(null)
                            .readyAt(null)
                            .status(OrderStatus.PLACED.name())
                            .paymentMethod(PaymentMethod.GATEWAY_BALANCE.name())
                            .build();

                    return new PageImpl<>(List.of(orderSummary), Pageable.ofSize(filter.getSize()), 1);
                });

        when(checkoutApplicationService.checkout(any(CheckoutInput.class)))
                .thenReturn("01226N0640J7Q");

        when(buyNowApplicationService.buyNow(any(BuyNowInput.class)))
                .thenReturn("01226N0640J7Q");

    }

}