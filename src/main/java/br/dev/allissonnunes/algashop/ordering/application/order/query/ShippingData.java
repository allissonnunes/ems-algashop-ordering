package br.dev.allissonnunes.algashop.ordering.application.order.query;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
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
