package com.github.allisson95.algashop.ordering.application.customer.query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CustomerSummaryOutput(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String document,
        String phone,
        LocalDate birthDate,
        Integer loyaltyPoints,
        Instant registeredAt,
        Instant archivedAt,
        Boolean promotionNotificationsAllowed,
        Boolean archived
) {

}
