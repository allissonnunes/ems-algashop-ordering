package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.*;
import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder(toBuilder = true)
public record Billing(FullName fullName, Document document, Phone phone, Email email, Address address) {

    public Billing {
        requireNonNull(fullName, "fullName cannot be null");
        requireNonNull(document, "document cannot be null");
        requireNonNull(phone, "phone cannot be null");
        requireNonNull(email, "email cannot be null");
        requireNonNull(address, "address cannot be null");
    }

}
