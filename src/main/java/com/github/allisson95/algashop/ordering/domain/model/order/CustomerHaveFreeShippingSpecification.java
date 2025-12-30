package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.Specification;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import com.github.allisson95.algashop.ordering.domain.model.customer.LoyaltyPoints;

import static com.github.allisson95.algashop.ordering.domain.model.Specification.where;
import static java.util.Objects.requireNonNull;

public class CustomerHaveFreeShippingSpecification implements Specification<Customer> {

    private final CustomerHasOrderedEnoughAtYearSpecification hasOrderedEnoughAtYear;

    private final CustomerHasEnoughLoyaltyPointsSpecification hasEnoughBasicLoyaltyPoints;

    private final CustomerHasEnoughLoyaltyPointsSpecification hasEnoughPremiumLoyaltyPoints;

    public CustomerHaveFreeShippingSpecification(
            final Orders orders,
            final long salesQuantityForFreeShipping,
            final LoyaltyPoints basicLoyaltyPoints,
            final LoyaltyPoints premiumLoyaltyPoints
    ) {
        requireNonNull(orders, "orders cannot be null");
        requireNonNull(basicLoyaltyPoints, "basicLoyaltyPoints cannot be null");
        requireNonNull(premiumLoyaltyPoints, "premiumLoyaltyPoints cannot be null");

        this.hasOrderedEnoughAtYear = new CustomerHasOrderedEnoughAtYearSpecification(orders, salesQuantityForFreeShipping);
        this.hasEnoughBasicLoyaltyPoints = new CustomerHasEnoughLoyaltyPointsSpecification(basicLoyaltyPoints);
        this.hasEnoughPremiumLoyaltyPoints = new CustomerHasEnoughLoyaltyPointsSpecification(premiumLoyaltyPoints);
    }

    @Override
    public boolean isSatisfiedBy(final Customer customer) {
        return where(hasEnoughBasicLoyaltyPoints
                .and(hasOrderedEnoughAtYear)
                .or(hasEnoughPremiumLoyaltyPoints))
                .isSatisfiedBy(customer);
    }

}
