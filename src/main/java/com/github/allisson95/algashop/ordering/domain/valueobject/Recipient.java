package com.github.allisson95.algashop.ordering.domain.valueobject;

import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder(toBuilder = true)
public record Recipient(FullName fullName, Document document, Phone phone) {

    public Recipient {
        requireNonNull(fullName, "fullName cannot be null");
        requireNonNull(document, "document cannot be null");
        requireNonNull(phone, "phone cannot be null");
    }

}
