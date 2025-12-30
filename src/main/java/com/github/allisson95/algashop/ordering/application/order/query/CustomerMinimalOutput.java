package com.github.allisson95.algashop.ordering.application.order.query;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerMinimalOutput(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String document,
        String phone
) {

}
