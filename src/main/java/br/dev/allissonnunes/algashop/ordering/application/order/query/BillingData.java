package br.dev.allissonnunes.algashop.ordering.application.order.query;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
import lombok.Builder;

@Builder
public record BillingData(
        String firstName,
        String lastName,
        String document,
        String phone,
        String email,
        AddressData address
) {

}
