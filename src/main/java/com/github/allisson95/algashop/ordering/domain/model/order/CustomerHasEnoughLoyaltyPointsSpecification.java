package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.Specification;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import com.github.allisson95.algashop.ordering.domain.model.customer.LoyaltyPoints;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerHasEnoughLoyaltyPointsSpecification implements Specification<Customer> {

    private final LoyaltyPoints minimumPoints;

    @Override
    public boolean isSatisfiedBy(final Customer customer) {
        return customer.getLoyaltyPoints().compareTo(minimumPoints) >= 0;
    }

}
