package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.ContractBaseExtension;
import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderQueryService;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(MapStructTestConfiguration.class)
@WebMvcTest(OrderController.class)
@ExtendWith(ContractBaseExtension.class)
class OrderBase {

    @MockitoBean
    private OrderQueryService orderQueryService;

    @BeforeEach
    void setUp() {
        final String validOrderId = "01226N0640J7Q";
        when(orderQueryService.findById(eq(validOrderId)))
                .thenReturn(OrderDetailOutputTestDataBuilder.placedOrder(validOrderId).build());

        final String notFoundOrderId = "01226N0693HDH";
        when(orderQueryService.findById(eq(notFoundOrderId)))
                .thenThrow(new OrderNotFoundException(new OrderId(notFoundOrderId)));
    }

}