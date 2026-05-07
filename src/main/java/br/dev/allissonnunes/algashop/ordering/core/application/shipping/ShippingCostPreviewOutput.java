package br.dev.allissonnunes.algashop.ordering.core.application.shipping;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ShippingCostPreviewOutput(BigDecimal cost, LocalDate expectedDate) {

}
