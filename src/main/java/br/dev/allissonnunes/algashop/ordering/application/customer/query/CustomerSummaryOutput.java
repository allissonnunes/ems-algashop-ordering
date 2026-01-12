package br.dev.allissonnunes.algashop.ordering.application.customer.query;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
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
