package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.web.customer;

import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.*;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.web.PageModel;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.security.SecurityAnnotations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequestUri;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
class CustomerController {

    private final ForManagingCustomers forManagingCustomers;

    private final ForQueryingCustomers forQueryingCustomers;

    private final ForQueryingShoppingCarts shoppingCartQueryService;

    @SecurityAnnotations.CanWriteCustomers
    @PostMapping
    ResponseEntity<CustomerOutput> registerCustomer(@RequestBody final @Valid CustomerInput customerInput) {
        final var customerId = forManagingCustomers.create(customerInput);

        final var location = fromCurrentRequestUri()
                .path("/{customerId}")
                .buildAndExpand(customerId)
                .toUri();

        return ResponseEntity
                .created(location)
                .body(forQueryingCustomers.findById(customerId));
    }

    @SecurityAnnotations.CanReadCustomers
    @GetMapping
    PageModel<CustomerSummaryOutput> getAllCustomers(final CustomerFilter filter) {
        return PageModel.of(forQueryingCustomers.filter(filter));
    }

    @SecurityAnnotations.CanReadCustomers
    @GetMapping("/{customerId}")
    ResponseEntity<CustomerOutput> getCustomerById(@PathVariable final UUID customerId) {
        return ResponseEntity.ok(forQueryingCustomers.findById(customerId));
    }

    @SecurityAnnotations.CanReadShoppingCarts
    @GetMapping("/{customerId}/shopping-cart")
    ResponseEntity<ShoppingCartOutput> getCustomerShoppingCartById(@PathVariable final UUID customerId) {
        return ResponseEntity.ok(shoppingCartQueryService.findByCustomerId(customerId));
    }

    @SecurityAnnotations.CanWriteCustomers
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerOutput> updateCustomerById(@PathVariable final UUID customerId, @RequestBody final @Valid CustomerUpdateInput customerUpdateInput) {
        forManagingCustomers.update(customerId, customerUpdateInput);
        final var updatedCustomer = forQueryingCustomers.findById(customerId);
        return ResponseEntity.ok(updatedCustomer);
    }

    @SecurityAnnotations.CanWriteCustomers
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable final UUID customerId) {
        forManagingCustomers.archive(customerId);
        return ResponseEntity.noContent().build();
    }

}
