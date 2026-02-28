package br.dev.allissonnunes.algashop.ordering.core.application.order.query;

import br.dev.allissonnunes.algashop.ordering.core.application.commons.AddressData;
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
