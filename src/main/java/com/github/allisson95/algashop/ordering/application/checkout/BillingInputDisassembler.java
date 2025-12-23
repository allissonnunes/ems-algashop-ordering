package com.github.allisson95.algashop.ordering.application.checkout;

import com.github.allisson95.algashop.ordering.application.utility.Mapper;
import com.github.allisson95.algashop.ordering.domain.model.commons.*;
import com.github.allisson95.algashop.ordering.domain.model.order.Billing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingInputDisassembler {

    private final Mapper mapper;

    public Billing toDomainModel(final BillingData billing) {
        return Billing.builder()
                .fullName(new FullName(billing.firstName(), billing.lastName()))
                .document(new Document(billing.document()))
                .phone(new Phone(billing.phone()))
                .email(new Email(billing.email()))
                .address(mapper.convert(billing.address(), Address.class))
                .build();
    }

}
