package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.ContractBaseExtension;
import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartOutputTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(MapStructTestConfiguration.class)
@WebMvcTest(ShoppingCartController.class)
@ExtendWith(ContractBaseExtension.class)
class ShoppingCartBase {

    @MockitoBean
    private ShoppingCartQueryService shoppingCartQueryService;

    @MockitoBean
    private ShoppingCartManagementApplicationService service;

    @BeforeEach
    void setUp() {
        final UUID customerId = new CustomerId().value();
        when(service.createNew(any(UUID.class)))
                .thenReturn(customerId);
        when(shoppingCartQueryService.findById(eq(customerId)))
                .thenReturn(ShoppingCartOutputTestDataBuilder
                        .emptyShoppingCart()
                        .customerId(customerId)
                        .build()
                );
    }

}