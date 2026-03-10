package br.dev.allissonnunes.algashop.ordering.core.application.customer;

import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.CustomerFilter;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.CustomerOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.CustomerSummaryOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.ForQueryingCustomers;
import br.dev.allissonnunes.algashop.ordering.core.ports.out.customer.ForObtainingCustomers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class CustomerQueryService implements ForQueryingCustomers {

    private final ForObtainingCustomers forObtainingCustomers;

    @Override
    public CustomerOutput findById(final UUID customerId) {
        return forObtainingCustomers.findById(customerId);
    }

    @Override
    public Page<CustomerSummaryOutput> filter(final CustomerFilter filter) {
        return forObtainingCustomers.filter(filter);
    }

}
