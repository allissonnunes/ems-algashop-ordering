package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.Specification;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.LoyaltyPoints;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerHasEnoughLoyaltyPointsSpecification implements Specification<Customer> {

    private final LoyaltyPoints minimumPoints;

    @Override
    public boolean isSatisfiedBy(final Customer customer) {
        return customer.getLoyaltyPoints().compareTo(minimumPoints) >= 0;
    }

}
