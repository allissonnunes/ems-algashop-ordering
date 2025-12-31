package com.github.allisson95.algashop.ordering.application.order.query;

import lombok.Builder;

@Builder
public record RecipientData(
        String firstName,
        String lastName,
        String document,
        String phone
) {

}
