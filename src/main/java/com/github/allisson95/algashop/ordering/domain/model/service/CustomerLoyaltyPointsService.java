package com.github.allisson95.algashop.ordering.domain.model.service;

import com.github.allisson95.algashop.ordering.domain.model.entity.Customer;
import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.exception.CantAddLoyaltyPointsIfOrderIsNotReady;
import com.github.allisson95.algashop.ordering.domain.model.exception.OrderNotBelongsToCustomerException;
import com.github.allisson95.algashop.ordering.domain.model.utility.DomainService;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Money;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

@DomainService
public class CustomerLoyaltyPointsService {

    private static final LoyaltyPoints BASE_POINTS = new LoyaltyPoints(5);

    private static final Money EXPECTED_AMOUNT_TO_GIVE_POINTS = new Money("1000");

    public void addPoints(final Customer customer, final Order order) {
        requireNonNull(customer, "customer cannot be null");
        requireNonNull(order, "order cannot be null");

        if (!customer.id().equals(order.customerId())) {
            throw new OrderNotBelongsToCustomerException();
        }

        if (!order.isReady()) {
            throw new CantAddLoyaltyPointsIfOrderIsNotReady();
        }

        if (hasEnoughValueToEarnPoints(order)) {
            final LoyaltyPoints pointsEarned = calculatePoints(order);
            customer.addLoyaltyPoints(pointsEarned);
        }
    }

    private LoyaltyPoints calculatePoints(final Order order) {
        final BigDecimal pointCalculationFactor = order.totalAmount().value().divideToIntegralValue(EXPECTED_AMOUNT_TO_GIVE_POINTS.value());
        return new LoyaltyPoints(pointCalculationFactor.intValue() * BASE_POINTS.value());
    }

    private boolean hasEnoughValueToEarnPoints(final Order order) {
        return order.totalAmount().compareTo(EXPECTED_AMOUNT_TO_GIVE_POINTS) >= 0;
    }

}
