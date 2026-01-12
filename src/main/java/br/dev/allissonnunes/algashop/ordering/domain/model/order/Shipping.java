package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Address;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import lombok.Builder;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

@Builder(toBuilder = true)
public record Shipping(Recipient recipient, Address address, Money cost, LocalDate expectedDeliveryDate) {

    public Shipping {
        requireNonNull(recipient, "recipient cannot be null");
        requireNonNull(address, "address cannot be null");
        requireNonNull(cost, "cost cannot be null");
        requireNonNull(expectedDeliveryDate, "expectedDeliveryDate cannot be null");
    }

}
