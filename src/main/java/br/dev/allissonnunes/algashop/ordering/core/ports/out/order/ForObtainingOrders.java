package br.dev.allissonnunes.algashop.ordering.core.ports.out.order;

import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderDetailOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderFilter;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderSummaryOutput;
import org.springframework.data.domain.Page;

public interface ForObtainingOrders {

    OrderDetailOutput findById(String orderId);

    Page<OrderSummaryOutput> filter(OrderFilter filter);

}
