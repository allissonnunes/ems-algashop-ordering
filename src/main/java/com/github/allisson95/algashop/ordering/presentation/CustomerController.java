package com.github.allisson95.algashop.ordering.presentation;

import com.github.allisson95.algashop.ordering.application.customer.management.CustomerInput;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerUpdateInput;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerFilter;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutput;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerQueryService;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerSummaryOutput;
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

    private final CustomerManagementApplicationService customerManagementApplicationService;

    private final CustomerQueryService customerQueryService;

    @PostMapping
    ResponseEntity<CustomerOutput> registerCustomer(@RequestBody final @Valid CustomerInput customerInput) {
        final var customerId = customerManagementApplicationService.create(customerInput);

        final var location = fromCurrentRequestUri()
                .path("/{customerId}")
                .buildAndExpand(customerId)
                .toUri();

        return ResponseEntity
                .created(location)
                .body(customerQueryService.findById(customerId));
    }

    @GetMapping
    PageModel<CustomerSummaryOutput> getAllCustomers(final CustomerFilter filter) {
        return PageModel.of(customerQueryService.filter(filter));
    }

    @GetMapping("/{customerId}")
    ResponseEntity<CustomerOutput> getCustomerById(@PathVariable final UUID customerId) {
        return ResponseEntity.ok(customerQueryService.findById(customerId));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerOutput> updateCustomerById(@PathVariable final UUID customerId, @RequestBody final @Valid CustomerUpdateInput customerUpdateInput) {
        customerManagementApplicationService.update(customerId, customerUpdateInput);
        final var updatedCustomer = customerQueryService.findById(customerId);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable final UUID customerId) {
        customerManagementApplicationService.archive(customerId);
        return ResponseEntity.noContent().build();
    }

}
