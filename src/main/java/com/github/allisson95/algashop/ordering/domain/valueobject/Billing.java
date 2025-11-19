package com.github.allisson95.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.util.Objects;

@Builder(toBuilder = true)
public record Billing(FullName fullName, Document document, Phone phone, Email email, Address address) {

    public Billing {
        Objects.requireNonNull(fullName, "fullName cannot be null");
        Objects.requireNonNull(document, "document cannot be null");
        Objects.requireNonNull(phone, "phone cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(address, "address cannot be null");
    }

}
