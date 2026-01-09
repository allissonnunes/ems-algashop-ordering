package com.github.allisson95.algashop.ordering.presentation;

import com.github.allisson95.algashop.ordering.application.customer.management.CustomerInput;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerFilter;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutput;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerQueryService;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
class CustomerController {

    private final CustomerManagementApplicationService customerManagementApplicationService;

    private final CustomerQueryService customerQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CustomerOutput registerCustomer(@RequestBody final @Valid CustomerInput customerInput) {
        final var customerId = customerManagementApplicationService.create(customerInput);
        return customerQueryService.findById(customerId);
    }

    @GetMapping
    PageModel<CustomerSummaryOutput> getAllCustomers(final CustomerFilter filter) {
        return PageModel.of(customerQueryService.filter(filter));
    }

}
