package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Document;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.FullName;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Phone;
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
