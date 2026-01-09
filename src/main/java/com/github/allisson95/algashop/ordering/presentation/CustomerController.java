package com.github.allisson95.algashop.ordering.presentation;

import com.github.allisson95.algashop.ordering.application.customer.management.CustomerInput;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
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
                .path("/{id}")
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

    @GetMapping("/{id}")
    ResponseEntity<CustomerOutput> getCustomerById(@PathVariable final UUID id) {
        return ResponseEntity.ok(customerQueryService.findById(id));
    }

}
