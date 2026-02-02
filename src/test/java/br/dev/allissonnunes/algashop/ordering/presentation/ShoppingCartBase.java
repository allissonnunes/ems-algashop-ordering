package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.ContractBaseExtension;
import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.management.ShoppingCartItemInput;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartOutputTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
        final UUID shoppingCartId = new ShoppingCartId().value();
        when(service.createNew(any(UUID.class)))
                .thenReturn(shoppingCartId);
        when(shoppingCartQueryService.findById(eq(shoppingCartId)))
                .thenReturn(ShoppingCartOutputTestDataBuilder.emptyShoppingCart().build());

        final UUID getShoppingCartById = UUID.fromString("277297bf-e586-4389-9f21-b3ce0c3f6580");
        when(shoppingCartQueryService.findById(eq(getShoppingCartById)))
                .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCart().id(getShoppingCartById).build());

        final UUID getShoppingCartByIdNotFound = UUID.fromString("019c1b89-6a93-798a-a9ef-8a4e6eb71040");
        when(shoppingCartQueryService.findById(eq(getShoppingCartByIdNotFound)))
                .thenThrow(new ShoppingCartNotFoundException(new ShoppingCartId(getShoppingCartByIdNotFound)));

        doNothing()
                .when(service)
                .delete(eq(UUID.fromString("019c1ba8-2713-77d7-94a0-9ce447ca9e89")));

        doNothing()
                .when(service)
                .empty(eq(UUID.fromString("019c1ba8-2713-77d7-94a0-9ce447ca9e89")));

        final UUID deleteShoppingCartByIdNotFound = UUID.fromString("019c1bac-f505-7266-8ad0-8889f319e8da");
        doThrow(new ShoppingCartNotFoundException(new ShoppingCartId(deleteShoppingCartByIdNotFound)))
                .when(service)
                .delete(eq(deleteShoppingCartByIdNotFound));

        doNothing()
                .when(service)
                .addItem(any(ShoppingCartItemInput.class));

        doNothing()
                .when(service)
                .removeItem(
                        eq(UUID.fromString("019c1bb7-c1ff-759d-83f7-c1d294c09643")),
                        eq(UUID.fromString("019c1bba-e3f6-720a-96f7-134682ea709d")));
    }

}