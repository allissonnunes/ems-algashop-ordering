package com.github.allisson95.algashop.ordering.infrastructure.persistence.order;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class ShippingEmbeddable {

    @Embedded
    private RecipientEmbeddable recipient;

    @Embedded
    private AddressEmbeddable address;

    private BigDecimal cost;

    private LocalDate expectedDeliveryDate;

}
