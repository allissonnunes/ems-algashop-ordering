package br.dev.allissonnunes.algashop.ordering.core.application.order;

import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderDetailOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderFilter;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderSummaryOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.out.order.ForObtainingOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class OrderQueryService implements ForQueryingOrders {

    private final ForObtainingOrders forObtainingOrders;

    @Override
    public OrderDetailOutput findById(final String orderId) {
        return forObtainingOrders.findById(orderId);
    }

    @Override
    public Page<OrderSummaryOutput> filter(final OrderFilter filter) {
        return forObtainingOrders.filter(filter);
    }

}
