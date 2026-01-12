package br.dev.allissonnunes.algashop.ordering.infrastructure.shipping.client.fake;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Address;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.ZipCode;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import org.springframework.stereotype.Component;

@Component
class FixedOriginAddressService implements OriginAddressService {

    @Override
    public Address originAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("1134")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .build();
    }

}
