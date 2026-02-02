package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import br.dev.allissonnunes.algashop.ordering.presentation.model.ShoppingCartInput;
import br.dev.allissonnunes.algashop.ordering.presentation.model.ShoppingCartItemListModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequestUri;

@RestController
@RequestMapping("/api/v1/shopping-carts")
@RequiredArgsConstructor
class ShoppingCartController {

    private final ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    private final ShoppingCartQueryService shoppingCartQueryService;

    @PostMapping
    ResponseEntity<ShoppingCartOutput> registerShoppingCart(@RequestBody final @Valid ShoppingCartInput input) {
        final UUID shoppingCartId = shoppingCartManagementApplicationService.createNew(input.customerId());
        final ShoppingCartOutput shoppingCart = shoppingCartQueryService.findById(shoppingCartId);

        final var location = fromCurrentRequestUri().path("/{shoppingCartId}")
                .buildAndExpand(shoppingCartId)
                .toUri();

        return ResponseEntity.created(location).body(shoppingCart);
    }

    @GetMapping("/{shoppingCartId}")
    ResponseEntity<ShoppingCartOutput> getShoppingCartById(@PathVariable final UUID shoppingCartId) {
        return ResponseEntity.ok(shoppingCartQueryService.findById(shoppingCartId));
    }

    @GetMapping("/{shoppingCartId}/items")
    ResponseEntity<ShoppingCartItemListModel> getItemsFromShoppingCart(@PathVariable final UUID shoppingCartId) {
        final ShoppingCartOutput shoppingCartDetails = shoppingCartQueryService.findById(shoppingCartId);
        return ResponseEntity.ok(new ShoppingCartItemListModel(shoppingCartDetails.items()));
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteShoppingCartById(@PathVariable final UUID shoppingCartId) {
        shoppingCartManagementApplicationService.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAllItemsFromShoppingCart(@PathVariable final UUID shoppingCartId) {
        shoppingCartManagementApplicationService.empty(shoppingCartId);
    }

}
