package br.dev.allissonnunes.algashop.ordering.core.application.order;

import br.dev.allissonnunes.algashop.ordering.core.application.security.SecurityCheckApplicationService;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderDetailOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderFilter;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.OrderSummaryOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.out.order.ForObtainingOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class OrderQueryService implements ForQueryingOrders {

    private final ForObtainingOrders forObtainingOrders;

    private final SecurityCheckApplicationService securityCheckApplicationService;

    @Override
    public OrderDetailOutput findById(final String orderId) {
        final OrderDetailOutput order = forObtainingOrders.findById(orderId);
        if (!canAccess(order)) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }
        return order;
    }

    @Override
    public Page<OrderSummaryOutput> filter(final OrderFilter filter) {
        if (securityCheckApplicationService.isCustomer()) {
            filter.setCustomerId(securityCheckApplicationService.getAuthenticatedUserId());
        }
        return forObtainingOrders.filter(filter);
    }

    private boolean canAccess(final OrderDetailOutput order) {
        if (!securityCheckApplicationService.isCustomer() && securityCheckApplicationService.isAuthenticated()) {
            return true;
        }

        return securityCheckApplicationService.isCustomer()
                && securityCheckApplicationService.getAuthenticatedUserId().equals(order.customer().id());
    }

}
