package com.github.allisson95.algashop.ordering.application.customer.query;

import com.github.allisson95.algashop.ordering.application.commons.AddressData;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CustomerOutput(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String email,
        String phone,
        String document,
        boolean promotionNotificationsAllowed,
        Integer loyaltyPoints,
        Instant registeredAt,
        Boolean archived,
        Instant archivedAt,
        AddressData address
) {

}
