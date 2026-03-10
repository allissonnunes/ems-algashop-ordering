package br.dev.allissonnunes.algashop.ordering.core.ports.in.order;

import br.dev.allissonnunes.algashop.ordering.core.ports.commons.AddressData;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ShippingData(
        BigDecimal cost,
        LocalDate expectedDeliveryDate,
        RecipientData recipient,
        AddressData address
) {

}
