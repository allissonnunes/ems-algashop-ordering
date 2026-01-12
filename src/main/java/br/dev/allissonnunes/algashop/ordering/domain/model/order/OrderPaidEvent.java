package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;

import java.time.Instant;

public record OrderPaidEvent(OrderId orderId, CustomerId customerId, Instant paidOn) {

}
