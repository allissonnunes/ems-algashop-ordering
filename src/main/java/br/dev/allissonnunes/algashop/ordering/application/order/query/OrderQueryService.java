package br.dev.allissonnunes.algashop.ordering.application.order.query;

import org.springframework.data.domain.Page;

public interface OrderQueryService {

    OrderDetailOutput findById(String orderId);

    Page<OrderSummaryOutput> filter(OrderFilter filter);

}
