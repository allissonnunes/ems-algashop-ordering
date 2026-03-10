package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ForManagingShoppingCarts;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemInput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.UnprocessableContentException;
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
public class ShoppingCartController {

    private final ForManagingShoppingCarts forManagingShoppingCarts;

    private final ForQueryingShoppingCarts forQueryingShoppingCarts;

    @PostMapping
    ResponseEntity<ShoppingCartOutput> registerShoppingCart(@RequestBody final @Valid ShoppingCartInput input) {
        final UUID shoppingCartId;
        try {
            shoppingCartId = forManagingShoppingCarts.createNew(input.customerId());
        } catch (final DomainEntityNotFoundException e) {
            throw new UnprocessableContentException(e.getMessage(), e);
        }
        final ShoppingCartOutput shoppingCart = forQueryingShoppingCarts.findById(shoppingCartId);

        final var location = fromCurrentRequestUri().path("/{shoppingCartId}")
                .buildAndExpand(shoppingCartId)
                .toUri();

        return ResponseEntity.created(location).body(shoppingCart);
    }

    @GetMapping("/{shoppingCartId}")
    ResponseEntity<ShoppingCartOutput> getShoppingCartById(@PathVariable final UUID shoppingCartId) {
        return ResponseEntity.ok(forQueryingShoppingCarts.findById(shoppingCartId));
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteShoppingCartById(@PathVariable final UUID shoppingCartId) {
        forManagingShoppingCarts.delete(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    ResponseEntity<ShoppingCartItemListModel> getItemsFromShoppingCart(@PathVariable final UUID shoppingCartId) {
        final ShoppingCartOutput shoppingCartDetails = forQueryingShoppingCarts.findById(shoppingCartId);
        return ResponseEntity.ok(new ShoppingCartItemListModel(shoppingCartDetails.items()));
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void addOrUpdateItemToShoppingCart(@PathVariable final UUID shoppingCartId, @RequestBody final @Valid ShoppingCartItemInput input) {
        final ShoppingCartItemInput updatedInput = input.toBuilder().shoppingCartId(shoppingCartId).build();
        try {
            forManagingShoppingCarts.addItem(updatedInput);
        } catch (final ProductNotFoundException e) {
            throw new UnprocessableContentException(e.getMessage(), e);
        }
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAllItemsFromShoppingCart(@PathVariable final UUID shoppingCartId) {
        forManagingShoppingCarts.empty(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteItemFromShoppingCart(@PathVariable final UUID shoppingCartId, @PathVariable final UUID itemId) {
        forManagingShoppingCarts.removeItem(shoppingCartId, itemId);
    }

}
