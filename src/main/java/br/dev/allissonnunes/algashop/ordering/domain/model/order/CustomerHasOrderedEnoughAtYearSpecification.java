package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.Specification;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import lombok.RequiredArgsConstructor;

import java.time.Year;

@RequiredArgsConstructor
public class CustomerHasOrderedEnoughAtYearSpecification implements Specification<Customer> {

    private final Orders orders;

    private final long minSalesQuantity;

    @Override
    public boolean isSatisfiedBy(final Customer customer) {
        return orders.salesQuantityByCustomerInYear(customer.getId(), Year.now()) >= minSalesQuantity;
    }

}
