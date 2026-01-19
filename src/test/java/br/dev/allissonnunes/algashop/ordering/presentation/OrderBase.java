package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.ContractBaseExtension;
import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderQueryService;
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
        final String orderId = "01226N0640J7Q";
        when(orderQueryService.findById(eq(orderId)))
                .thenReturn(OrderDetailOutputTestDataBuilder.placedOrder(orderId).build());
    }

}