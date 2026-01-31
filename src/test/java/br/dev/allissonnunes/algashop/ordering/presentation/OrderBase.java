package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.ContractBaseExtension;
import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.application.checkout.BuyNowApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.checkout.BuyNowInput;
import br.dev.allissonnunes.algashop.ordering.application.checkout.CheckoutApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.checkout.CheckoutInput;
import br.dev.allissonnunes.algashop.ordering.application.order.query.CustomerMinimalOutput;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderFilter;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderQueryService;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderSummaryOutput;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderNotFoundException;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderStatus;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.PaymentMethod;
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
@ExtendWith(ContractBaseExtension.class)
class OrderBase {

    @MockitoBean
    private OrderQueryService orderQueryService;

    @MockitoBean
    private CheckoutApplicationService checkoutApplicationService;

    @MockitoBean
    private BuyNowApplicationService buyNowApplicationService;

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