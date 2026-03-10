package br.dev.allissonnunes.algashop.ordering.core.ports.in.order;

import org.springframework.data.domain.Page;

public interface ForQueryingOrders {

    OrderDetailOutput findById(String orderId);

    Page<OrderSummaryOutput> filter(OrderFilter filter);

}
