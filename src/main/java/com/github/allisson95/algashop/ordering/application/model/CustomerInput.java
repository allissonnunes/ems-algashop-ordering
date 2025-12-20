package com.github.allisson95.algashop.ordering.application.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerInput(
        String firstName,
        String lastName,
        LocalDate birthDate,
        String email,
        String phone,
        String document,
        Boolean promotionNotificationsAllowed,
        AddressData address
) {

}
