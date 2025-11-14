package com.github.allisson95.algashop.ordering.domain.valueobject;

import lombok.Builder;

import static com.github.allisson95.algashop.ordering.domain.validator.Validators.requireNonBlank;
import static java.util.Objects.requireNonNull;

@Builder(toBuilder = true)
public record Address(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        ZipCode zipCode
) {

    public Address {
        requireNonBlank(street, "street cannot be blank");
        requireNonBlank(number, "number cannot be blank");
        requireNonBlank(neighborhood, "neighborhood cannot be blank");
        requireNonBlank(city, "city cannot be blank");
        requireNonBlank(state, "state cannot be blank");
        requireNonNull(zipCode, "zipCode cannot be null");
    }

}
