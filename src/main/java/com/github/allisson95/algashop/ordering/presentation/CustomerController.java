package com.github.allisson95.algashop.ordering.presentation;

import com.github.allisson95.algashop.ordering.application.customer.management.CustomerInput;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutput;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CustomerOutput create(@RequestBody final CustomerInput customerInput) {
        return CustomerOutput.builder()
                .id(UUID.randomUUID())
                .firstName(customerInput.firstName())
                .lastName(customerInput.lastName())
                .birthDate(customerInput.birthDate())
                .email(customerInput.email())
                .phone(customerInput.phone())
                .document(customerInput.document())
                .promotionNotificationsAllowed(customerInput.promotionNotificationsAllowed())
                .loyaltyPoints(0)
                .registeredAt(Instant.now())
                .archived(false)
                .archivedAt(null)
                .address(customerInput.address())
                .build();
    }

}
