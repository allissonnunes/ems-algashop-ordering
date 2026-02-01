package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import br.dev.allissonnunes.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import br.dev.allissonnunes.algashop.ordering.presentation.model.ShoppingCartInput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

}
