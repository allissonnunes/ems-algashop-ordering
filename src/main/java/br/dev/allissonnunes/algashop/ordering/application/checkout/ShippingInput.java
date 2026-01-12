package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
import br.dev.allissonnunes.algashop.ordering.application.order.query.RecipientData;
import lombok.Builder;

@Builder
public record ShippingInput(
        RecipientData recipient,
        AddressData address
) {

}
