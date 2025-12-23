package com.github.allisson95.algashop.ordering.application.checkout;

import lombok.Builder;

@Builder
public record RecipientData(
        String firstName,
        String lastName,
        String document,
        String phone
) {

}
