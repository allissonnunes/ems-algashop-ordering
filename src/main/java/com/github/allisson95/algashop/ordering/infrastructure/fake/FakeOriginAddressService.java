package com.github.allisson95.algashop.ordering.infrastructure.fake;

import com.github.allisson95.algashop.ordering.domain.model.service.OriginAddressService;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Address;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.ZipCode;
import org.springframework.stereotype.Component;

@Component
class FakeOriginAddressService implements OriginAddressService {

    @Override
    public Address originAddress() {
        return Address.builder()
                .street("Fake street")
                .number("123")
                .complement("fake address")
                .neighborhood("fake")
                .city("Fake City")
                .state("FK")
                .zipCode(new ZipCode("1234567"))
                .build();
    }

}
