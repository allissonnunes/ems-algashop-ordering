package br.dev.allissonnunes.algashop.ordering.core.ports.out.customer;

import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.CustomerFilter;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.CustomerOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.CustomerSummaryOutput;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ForObtainingCustomers {

    CustomerOutput findById(UUID customerId);

    Page<CustomerSummaryOutput> filter(CustomerFilter filter);

}
